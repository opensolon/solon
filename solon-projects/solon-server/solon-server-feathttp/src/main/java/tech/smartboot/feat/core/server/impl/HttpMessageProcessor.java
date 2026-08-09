/*
 *  Copyright (C) [2024] smartboot [zhengjunweimail@163.com]
 *
 *  企业用户未经smartboot组织特别许可，需遵循Apache-2.0开源协议合理合法使用本项目。
 *
 *   Enterprise users are required to use this project reasonably
 *   and legally in accordance with the Apache-2.0 open source agreement
 *  without special permission from the smartboot organization.
 */

package tech.smartboot.feat.core.server.impl;

import io.github.smartboot.socket.AbstractMessageProcessor;
import io.github.smartboot.socket.StateMachineEnum;
import io.github.smartboot.socket.transport.AioSession;
import tech.smartboot.feat.Feat;
import tech.smartboot.feat.core.common.*;
import tech.smartboot.feat.core.common.exception.HttpException;
import tech.smartboot.feat.core.common.io.FeatOutputStream;
import tech.smartboot.feat.core.common.io.ReadListener;
import tech.smartboot.feat.core.common.logging.Logger;
import tech.smartboot.feat.core.common.logging.LoggerFactory;
import tech.smartboot.feat.core.server.HttpHandler;
import tech.smartboot.feat.core.server.ServerOptions;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
public final class HttpMessageProcessor extends AbstractMessageProcessor<HttpEndpoint> {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpMessageProcessor.class);
    private static final int MAX_LENGTH = 255 * 1024;
    private static final HttpHandler BASE_HTTP_HANDLER = request -> request.getResponse().write("Hello Feat".getBytes(StandardCharsets.UTF_8));
    private final ServerOptions options;
    private HttpHandler httpServerHandler;

    public HttpMessageProcessor(ServerOptions options) {
        this.options = options;
    }

    @Override
    public void process0(AioSession session, HttpEndpoint request) {
        DecodeContext decodeContext = request.getDecodeContext();
        HttpHandler httpHandler = request.getServerHandler();
        if (httpHandler == null) {
            request.setServerHandler(httpServerHandler == null ? BASE_HTTP_HANDLER : httpServerHandler);
        }
        try {
            switch (decodeContext.getState()) {
                case DecodeContext.STATE_HEADER_CALLBACK: {
                    doHttpHeader(request);
                    if (request.getResponse().isClosed()) {
                        break;
                    } else {
                        decodeContext.setState(DecodeContext.STATE_BODY_READING_CALLBACK);
                    }
                }
                case DecodeContext.STATE_BODY_READING_CALLBACK: {
                    decodeContext.setState(DecodeContext.STATE_BODY_READING_MONITOR);
                    Upgrade upgrade = request.getUpgrade();
                    if (upgrade != null) {
                        upgrade.onBodyStream(session.readBuffer());
                        if (decodeContext.getState() == DecodeContext.STATE_BODY_ASYNC_READING_DONE) {
                            onBodyStream(request);
                        }
                    } else {
                        onBodyStream(request);
                    }
                    break;
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }


    public static void responseError(AbstractResponse response, Throwable throwable) {
        if (throwable instanceof HttpException) {
            HttpException httpException = (HttpException) throwable;
            responseError(response, httpException.getHttpStatus(), httpException.getMessage());
        } else if (throwable.getCause() != null) {
            responseError(response, throwable.getCause());
        } else {
            LOGGER.error("HttpError response exception", throwable);
            responseError(response, HttpStatus.INTERNAL_SERVER_ERROR, throwable.fillInStackTrace().toString());
        }
    }

    private static void responseError(AbstractResponse response, HttpStatus httpStatus, String desc) {
        try {
            if (response.isClosed()) {
                return;
            }
            response.setHttpStatus(httpStatus);
            OutputStream outputStream = response.getOutputStream();
            outputStream.write(("<center><h1>" + httpStatus.value() + " " + httpStatus.getReasonPhrase() + "</h1>" + desc + "<hr/><a target='_blank' href='https://smartboot.tech/'>feat</a>/" + Feat.VERSION + "&nbsp;|&nbsp; <a target='_blank' href='https://gitee.com/smartboot/feat'>Gitee</a></center>").getBytes());
        } catch (IOException e) {
            LOGGER.debug("HttpError response exception", e);
        } finally {
            response.close();
        }
    }

    private void doHttpHeader(HttpEndpoint request) throws IOException {
        methodCheck(request);
        uriCheck(request);
        request.getServerHandler().onHeaderComplete(request);
    }

    @Override
    public void stateEvent0(AioSession session, StateMachineEnum stateMachineEnum, Throwable throwable) {
//        if (throwable != null) {
//            throwable.printStackTrace();
//        }
        switch (stateMachineEnum) {
            case NEW_SESSION: {
                session.setAttachment(new HttpEndpoint(options, session));
                break;
            }
            case PROCESS_EXCEPTION:
                LOGGER.error("process exception", throwable);
                session.close();
                break;
            case SESSION_CLOSED: {
                HttpEndpoint request = session.getAttachment();
                try {
                    if (request.getServerHandler() != null) {
                        request.getServerHandler().onClose(request);
                    }
                    if (request.getUpgrade() != null) {
                        request.getUpgrade().destroy();
                    }
                } finally {
                    request.cancelHttpIdleTask();
                }
                break;
            }
            case DECODE_EXCEPTION: {
                LOGGER.warn("http decode exception,", throwable);
                HttpEndpoint request = session.getAttachment();
                responseError(request.getResponse(), throwable);
                break;
            }
            case INTERNAL_EXCEPTION: {
                LOGGER.error("internal exception", throwable);
                break;
            }
        }
    }

    public void httpServerHandler(HttpHandler httpServerHandler) {
        Objects.requireNonNull(httpServerHandler, "httpServerHandler");
        if (this.httpServerHandler != null) {
            throw new IllegalStateException("httpServerHandler has been set");
        }
        this.httpServerHandler = httpServerHandler;
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
    private void methodCheck(HttpEndpoint request) {
        if (request.getMethod() == null) {
            throw new HttpException(HttpStatus.NOT_IMPLEMENTED);
        }
    }

    /**
     * 1、客户端和服务器都必须支持 Host 请求头域。
     * 2、发送 HTTP/1.1 请求的客户端必须发送 Host 头域。
     * 3、如果 HTTP/1.1 请求不包括 Host 请求头域，服务器必须报告错误 400(Bad Request)。 --服务器必须接受绝对 URIs(absolute URIs)。
     */
    private void hostCheck(HttpEndpoint request) {
        if (request.getHost() == null) {
            throw new HttpException(HttpStatus.BAD_REQUEST);
        }
    }


    /**
     * RFC2616 3.2.1
     * HTTP 协议不对 URI 的长度作事先的限制，服务器必须能够处理任何他们提供资源的 URI，并 且应该能够处理无限长度的 URIs，这种无效长度的 URL 可能会在客户端以基于 GET 方式的 请求时产生。如果服务器不能处理太长的 URI 的时候，服务器应该返回 414 状态码(此状态码 代表 Request-URI 太长)。
     * 注:服务器在依赖大于 255 字节的 URI 时应谨慎，因为一些旧的客户或代理实现可能不支持这 些长度。
     */
    private void uriCheck(HttpEndpoint request) {
        String originalUri = request.getUri();
        if (FeatUtils.length(originalUri) > MAX_LENGTH) {
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
            if (uriIndex == FeatUtils.INDEX_NOT_FOUND) {
                request.setRequestURI("/");
            } else {
                request.setRequestURI(FeatUtils.substring(originalUri, uriIndex));
            }
        } else {
            request.setRequestURI(originalUri);
        }
    }

    public void onBodyStream(HttpEndpoint request) {
        AbstractResponse response = request.getResponse();
        try {
            CompletableFuture<Void> future = new CompletableFuture<>();
            if (request.getProtocol() == HttpProtocol.HTTP_11 && !HeaderValue.Connection.CLOSE.equals(request.getHeader(HeaderName.CONNECTION))) {
                request.setKeepAlive(true);
            } else if (request.getProtocol() == HttpProtocol.HTTP_10 && HeaderValue.Connection.KEEPALIVE.equals(request.getHeader(HeaderName.CONNECTION))) {
                request.setKeepAlive(true);
                response.setHeader(HeaderName.CONNECTION, HeaderValue.Connection.KEEPALIVE);
            }

            request.getServerHandler().handle(request, future);
            if (request.getUpgrade() == null || request.getInputStream().getReadListener() != null) {
                finishHttpHandle(request, future);
            }
        } catch (Throwable e) {
            HttpMessageProcessor.responseError(response, e);
        }
    }

    static boolean isKeepAlive(HttpEndpoint request) {
        return (request.getProtocol() == HttpProtocol.HTTP_11 && !HeaderValue.Connection.CLOSE.equals(request.getHeader(HeaderName.CONNECTION)))
                || (request.getProtocol() == HttpProtocol.HTTP_10 && HeaderValue.Connection.KEEPALIVE.equals(request.getHeader(HeaderName.CONNECTION)));
    }

    private void finishHttpHandle(HttpEndpoint abstractRequest, CompletableFuture<Void> future) throws IOException {
        if (future.isDone()) {
            if (future.isCompletedExceptionally()) {
                future.exceptionally(throwable -> {
                    HttpMessageProcessor.responseError(abstractRequest.getResponse(), throwable);
                    return null;
                });
            } else if (keepConnection(abstractRequest)) {
                finishResponse(abstractRequest);
            }
            return;
        }

        AioSession session = abstractRequest.getAioSession();
        ReadListener readListener = abstractRequest.getInputStream().getReadListener();
        if (readListener == null) {
            session.awaitRead();
        } else if (abstractRequest.getAioSession().readBuffer().hasRemaining()) {
            abstractRequest.getInputStream().getReadListener().onDataAvailable();
        }

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
            } catch (Exception e) {
                HttpMessageProcessor.responseError(response, e);
            } finally {
                if (readListener == null) {
                    session.signalRead();
                }
            }
        }).exceptionally(throwable -> {
            try {
                HttpMessageProcessor.responseError(response, throwable);
            } finally {
                if (readListener == null) {
                    session.signalRead();
                }
            }
            return null;
        });
    }

    private void finishResponse(HttpEndpoint abstractRequest) throws IOException {
        AbstractResponse response = abstractRequest.getResponse();
        //关闭本次请求的输出流
        FeatOutputStream bufferOutputStream = response.getOutputStream();
        if (!bufferOutputStream.isClosed()) {
            bufferOutputStream.close();
        }
        abstractRequest.reset();
    }

    private boolean keepConnection(HttpEndpoint request) {
        if (request.getResponse().isClosed()) {
            return false;
        }
        if (request.isKeepAlive() && request.getInputStream().isFinished()) {
            return true;
        }
        //非keepAlive或者 body部分未读取完毕,释放连接资源
        request.getResponse().close();
        return false;
    }
}
