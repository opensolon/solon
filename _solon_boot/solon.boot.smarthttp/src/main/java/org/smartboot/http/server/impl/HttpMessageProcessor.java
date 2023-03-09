/*******************************************************************************
 * Copyright (c) 2017-2021, org.smartboot. All rights reserved.
 * project name: smart-http
 * file name: HttpMessageProcessor.java
 * Date: 2021-02-07
 * Author: sandao (zhengjunweimail@163.com)
 ******************************************************************************/

package org.smartboot.http.server.impl;

import org.smartboot.http.common.enums.DecodePartEnum;
import org.smartboot.http.common.enums.HeaderNameEnum;
import org.smartboot.http.common.enums.HeaderValueEnum;
import org.smartboot.http.common.enums.HttpMethodEnum;
import org.smartboot.http.common.enums.HttpProtocolEnum;
import org.smartboot.http.common.enums.HttpStatus;
import org.smartboot.http.common.enums.HttpTypeEnum;
import org.smartboot.http.common.exception.HttpException;
import org.smartboot.http.common.logging.Logger;
import org.smartboot.http.common.logging.LoggerFactory;
import org.smartboot.http.common.utils.StringUtils;
import org.smartboot.http.server.HttpRequest;
import org.smartboot.http.server.HttpServerConfiguration;
import org.smartboot.http.server.HttpServerHandler;
import org.smartboot.http.server.WebSocketHandler;
import org.smartboot.socket.StateMachineEnum;
import org.smartboot.socket.extension.processor.AbstractMessageProcessor;
import org.smartboot.socket.transport.AioSession;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * @author 三刀
 * @version V1.0 , 2018/6/10
 */
public class HttpMessageProcessor extends AbstractMessageProcessor<Request> {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpMessageProcessor.class);
    private static final int MAX_LENGTH = 255 * 1024;
    private HttpServerConfiguration configuration;

    @Override
    public void process0(AioSession session, Request request) {
        RequestAttachment attachment = session.getAttachment();
        AbstractRequest abstractRequest = request.newAbstractRequest();
        AbstractResponse response = abstractRequest.getResponse();
        try {
            switch (request.getDecodePartEnum()) {
                case HEADER_FINISH:
                    doHttpHeader(request);
                    if (response.isClosed()) {
                        break;
                    }
                case BODY:
                    onHttpBody(request, session.readBuffer(), attachment);
                    if (response.isClosed() || request.getDecodePartEnum() != DecodePartEnum.FINISH) {
                        break;
                    }
                case FINISH: {
                    //消息处理
                    switch (request.getRequestType()) {
                        case WEBSOCKET:
                            handleWebSocketRequest(request.newWebsocketRequest());
                            break;
                        case HTTP:
                            handleHttpRequest(request.newHttpRequest());
                            break;
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void handleWebSocketRequest(WebSocketRequestImpl abstractRequest) throws IOException {
        CompletableFuture<Object> future = new CompletableFuture<>();
        abstractRequest.request.getServerHandler().handle(abstractRequest, abstractRequest.getResponse(), future);
        if (future.isDone()) {
            finishResponse(abstractRequest);
        } else {
            Thread thread = Thread.currentThread();
            AioSession session = abstractRequest.request.getAioSession();
            session.awaitRead();
            future.thenRun(() -> {
                try {
                    finishResponse(abstractRequest);
                    if (thread != Thread.currentThread()) {
                        session.writeBuffer().flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    abstractRequest.getResponse().close();
                } finally {
                    session.signalRead();
                }
            });
        }
    }

    private void handleHttpRequest(HttpRequestImpl abstractRequest) {
        AbstractResponse response = abstractRequest.getResponse();
        CompletableFuture<Object> future = new CompletableFuture<>();
        boolean keepAlive = isKeepAlive(abstractRequest, response);
        abstractRequest.setKeepAlive(keepAlive);
        try {
            abstractRequest.request.getServerHandler().handle(abstractRequest, response, future);
            finishHttpHandle(abstractRequest, future);
        } catch (HttpException e) {
            e.printStackTrace();
            responseError(response, HttpStatus.valueOf(e.getHttpCode()), e.getDesc());
        } catch (Exception e) {
            e.printStackTrace();
            responseError(response, HttpStatus.INTERNAL_SERVER_ERROR, e.fillInStackTrace().toString());
        }
    }

    private boolean isKeepAlive(AbstractRequest abstractRequest, AbstractResponse response) {
        boolean keepAlive = true;
        // http/1.0默认短连接，http/1.1默认长连接。此处用 == 性能更高
        if (HttpProtocolEnum.HTTP_10.getProtocol() == abstractRequest.getProtocol()) {
            keepAlive = HeaderValueEnum.KEEPALIVE.getName().equalsIgnoreCase(abstractRequest.getHeader(HeaderNameEnum.CONNECTION.getName()));
            if (keepAlive) {
                response.setHeader(HeaderNameEnum.CONNECTION.getName(), HeaderValueEnum.KEEPALIVE.getName());
            }
        }
        return keepAlive;
    }

    private static void responseError(AbstractResponse response, HttpStatus httpStatus, String desc) {
        try {
            response.setHttpStatus(httpStatus);
            response.getOutputStream().write(desc.getBytes());
        } catch (IOException e) {
            LOGGER.warn("HttpError response exception", e);
        } finally {
            response.close();
        }
    }

    private void finishHttpHandle(HttpRequestImpl abstractRequest, CompletableFuture<Object> future) throws IOException {
        if (future.isDone()) {
            if (keepConnection(abstractRequest)) {
                finishResponse(abstractRequest);
            }
        } else {
            AioSession session = abstractRequest.request.getAioSession();
            session.awaitRead();
            Thread thread = Thread.currentThread();
            AbstractResponse response = abstractRequest.getResponse();
            future.thenRun(() -> {
                try {
                    if (keepConnection(abstractRequest)) {
                        finishResponse(abstractRequest);
                        if (thread != Thread.currentThread()) {
                            session.writeBuffer().flush();
                        }
                    }
                } catch (HttpException e) {
                    e.printStackTrace();
                    responseError(response, HttpStatus.valueOf(e.getHttpCode()), e.getDesc());
                } catch (Exception e) {
                    e.printStackTrace();
                    responseError(response, HttpStatus.INTERNAL_SERVER_ERROR, e.fillInStackTrace().toString());
                } finally {
                    session.signalRead();
                }
            });
        }
    }

    private boolean keepConnection(HttpRequestImpl request) throws IOException {
        //非keepAlive或者 body部分未读取完毕,释放连接资源
        if (!request.isKeepAlive() || !HttpMethodEnum.GET.getMethod().equals(request.getMethod()) && request.getContentLength() > 0 && request.getInputStream().available() > 0) {
            request.getResponse().close();
            return false;
        }
        return true;
    }


    private void finishResponse(AbstractRequest abstractRequest) throws IOException {
        AbstractResponse response = abstractRequest.getResponse();
        //关闭本次请求的输出流
        if (!response.getOutputStream().isClosed()) {
            response.getOutputStream().close();
        }
        abstractRequest.reset();
    }


    private void onHttpBody(Request request, ByteBuffer readBuffer, RequestAttachment attachment) {
        if (request.getServerHandler().onBodyStream(readBuffer, request)) {
            request.setDecodePartEnum(DecodePartEnum.FINISH);
            if (request.getRequestType() == HttpTypeEnum.HTTP) {
                attachment.setDecoder(null);
            }
        } else if (readBuffer.hasRemaining()) {
            //半包,继续读数据
            attachment.setDecoder(HttpRequestProtocol.BODY_CONTINUE_DECODER);
        }
    }

    private void doHttpHeader(Request request) throws IOException {
        methodCheck(request);
        uriCheck(request);
        request.getServerHandler().onHeaderComplete(request);
        request.setDecodePartEnum(DecodePartEnum.BODY);
    }

    @Override
    public void stateEvent0(AioSession session, StateMachineEnum stateMachineEnum, Throwable throwable) {
        switch (stateMachineEnum) {
            case NEW_SESSION: {
                RequestAttachment attachment = new RequestAttachment(new Request(configuration, session));
                session.setAttachment(attachment);
                break;
            }
            case PROCESS_EXCEPTION:
                LOGGER.error("process exception", throwable);
                session.close();
                break;
            case SESSION_CLOSED: {
                RequestAttachment att = session.getAttachment();
                if (att.getRequest().getServerHandler() != null) {
                    att.getRequest().getServerHandler().onClose(att.getRequest());
                }
                break;
            }
            case DECODE_EXCEPTION: {
                LOGGER.warn("http decode exception,", throwable);
                RequestAttachment attachment = session.getAttachment();
                AbstractRequest abstractRequest = attachment.getRequest().newAbstractRequest();
                AbstractResponse response = abstractRequest.getResponse();
                if (throwable instanceof HttpException) {
                    responseError(response, HttpStatus.valueOf(((HttpException) throwable).getHttpCode()), ((HttpException) throwable).getDesc());
                } else {
                    responseError(response, HttpStatus.INTERNAL_SERVER_ERROR, throwable.fillInStackTrace().toString());
                }
                break;
            }
        }
    }

    public void httpServerHandler(HttpServerHandler httpServerHandler) {
        this.configuration.setHttpServerHandler(Objects.requireNonNull(httpServerHandler));
    }

    public void setWebSocketHandler(WebSocketHandler webSocketHandler) {
        this.configuration.setWebSocketHandler(Objects.requireNonNull(webSocketHandler));
    }

    /**
     * RFC2616 5.1.1 方法标记指明了在被 Request-URI 指定的资源上执行的方法。
     * 这种方法是大小写敏感的。 资源所允许的方法由 Allow 头域指定(14.7 节)。
     * 响应的返回码总是通知客户某个方法对当前资源是否是被允许的，因为被允许的方法能被动态的改变。
     * 如果服务器能理解某方法但此方法对请求资源不被允许的，
     * 那么源服务器应该返回 405 状态码(方法不允许);
     * 如果源服务器不能识别或没有实现某个方法，那么服务器应返回 501 状态码(没有实现)。
     * 方法 GET 和 HEAD 必须被所有一般的服务器支持。 所有其它的方法是可选的;
     * 然而，如果上面的方法都被实现， 这些方法遵循的语意必须和第 9 章指定的相同
     */
    private void methodCheck(HttpRequest request) {
        if (request.getMethod() == null) {
            throw new HttpException(HttpStatus.NOT_IMPLEMENTED);
        }
    }

    /**
     * 1、客户端和服务器都必须支持 Host 请求头域。
     * 2、发送 HTTP/1.1 请求的客户端必须发送 Host 头域。
     * 3、如果 HTTP/1.1 请求不包括 Host 请求头域，服务器必须报告错误 400(Bad Request)。 --服务器必须接受绝对 URIs(absolute URIs)。
     */
    private void hostCheck(Request request) {
        if (request.getHost() == null) {
            throw new HttpException(HttpStatus.BAD_REQUEST);
        }
    }


    /**
     * RFC2616 3.2.1
     * HTTP 协议不对 URI 的长度作事先的限制，服务器必须能够处理任何他们提供资源的 URI，并 且应该能够处理无限长度的 URIs，这种无效长度的 URL 可能会在客户端以基于 GET 方式的 请求时产生。如果服务器不能处理太长的 URI 的时候，服务器应该返回 414 状态码(此状态码 代表 Request-URI 太长)。
     * 注:服务器在依赖大于 255 字节的 URI 时应谨慎，因为一些旧的客户或代理实现可能不支持这 些长度。
     */
    private void uriCheck(Request request) {
        String originalUri = request.getUri();
        if (StringUtils.length(originalUri) > MAX_LENGTH) {
            throw new HttpException(HttpStatus.URI_TOO_LONG);
        }
        /**
         *http_URL = "http:" "//" host [ ":" port ] [ abs_path [ "?" query ]]
         *1. 如果 Request-URI 是绝对地址(absoluteURI)，那么主机(host)是 Request-URI 的 一部分。任何出现在请求里 Host 头域的值应当被忽略。
         *2. 假如 Request-URI 不是绝对地址(absoluteURI)，并且请求包括一个 Host 头域，则主 机(host)由该 Host 头域的值决定.
         *3. 假如由规则1或规则2定义的主机(host)对服务器来说是一个无效的主机(host)， 则应当以一个 400(坏请求)错误消息返回。
         */
        if (originalUri.charAt(0) == '/') {
            request.setRequestURI(originalUri);
            return;
        }
        int schemeIndex = originalUri.indexOf("://");
        if (schemeIndex > 0) {//绝对路径
            int uriIndex = originalUri.indexOf('/', schemeIndex + 3);
            if (uriIndex == StringUtils.INDEX_NOT_FOUND) {
                request.setRequestURI("/");
            } else {
                request.setRequestURI(StringUtils.substring(originalUri, uriIndex));
            }
            request.setScheme(StringUtils.substring(originalUri, 0, schemeIndex));
        } else {
            request.setRequestURI(originalUri);
        }
    }

    public void setConfiguration(HttpServerConfiguration configuration) {
        this.configuration = configuration;
    }
}
