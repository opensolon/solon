/*******************************************************************************
 * Copyright (c) 2017-2020, org.smartboot. All rights reserved.
 * project name: smart-http
 * file name: HttpMessageProcessor.java
 * Date: 2020-01-01
 * Author: sandao (zhengjunweimail@163.com)
 ******************************************************************************/

package org.smartboot.http.server;

import org.smartboot.http.HttpRequest;
import org.smartboot.http.HttpResponse;
import org.smartboot.http.WebSocketRequest;
import org.smartboot.http.WebSocketResponse;
import org.smartboot.http.enums.YesNoEnum;
import org.smartboot.http.logging.RunLogger;
import org.smartboot.http.server.handle.HandlePipeline;
import org.smartboot.http.server.handle.HttpHandle;
import org.smartboot.http.server.handle.Pipeline;
import org.smartboot.http.server.handle.WebSocketHandle;
import org.smartboot.http.utils.AttachKey;
import org.smartboot.http.utils.Attachment;
import org.smartboot.socket.MessageProcessor;
import org.smartboot.socket.StateMachineEnum;
import org.smartboot.socket.transport.AioSession;

import java.io.IOException;
import java.util.logging.Level;

/**
 * @author 三刀
 * @version V1.0 , 2018/6/10
 */
public class HttpMessageProcessor implements MessageProcessor<Request> {
    /**
     * HttpRequest附件Key
     */
    private final AttachKey<HttpRequestImpl> ATTACH_KEY_HTTP_REQUEST = AttachKey.valueOf("httpRequest");
    /**
     * Http消息处理管道
     */
    private final HandlePipeline<HttpRequest, HttpResponse> httpPipeline = new HandlePipeline<>();
    /**
     * Websocket处理管道
     */
    private final HandlePipeline<WebSocketRequest, WebSocketResponse> wsPipeline = new HandlePipeline<>();

    public HttpMessageProcessor() {
        httpPipeline.next(new HttpExceptionHandle()).next(new RFC2612RequestHandle());
        wsPipeline.next(new WebSocketHandSharkHandle());
    }

    @Override
    public void process(AioSession session, Request baseHttpRequest) {
        try {
            Attachment attachment = session.getAttachment();
            AbstractRequest request;
            AbstractResponse response;
            HandlePipeline pipeline;
            if (baseHttpRequest.isWebsocket() == YesNoEnum.Yes) {
                WebSocketRequestImpl webSocketRequest = attachment.get(HttpRequestProtocol.ATTACH_KEY_WS_REQ);
                request = webSocketRequest;
                response = webSocketRequest.getResponse();
                pipeline = wsPipeline;
            } else {
                HttpRequestImpl http11Request = attachment.get(ATTACH_KEY_HTTP_REQUEST);
                if (http11Request == null) {
                    http11Request = new HttpRequestImpl(baseHttpRequest);
                    attachment.put(ATTACH_KEY_HTTP_REQUEST, http11Request);
                }
                request = http11Request;
                response = http11Request.getResponse();
                pipeline = httpPipeline;
            }

            //消息处理
            pipeline.doHandle(request, response);

            //关闭本次请求的输出流
            if (!response.getOutputStream().isClosed()) {
                response.getOutputStream().close();
            }

            //response被closed,则断开TCP连接
            if (response.isClosed()) {
                session.close(false);
            } else {
                //复用长连接
                request.reset();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stateEvent(AioSession session, StateMachineEnum stateMachineEnum, Throwable throwable) {
        switch (stateMachineEnum) {
            case NEW_SESSION:
                Attachment attachment = new Attachment();
                attachment.put(HttpRequestProtocol.ATTACH_KEY_REQUEST, new Request(session));
                session.setAttachment(attachment);
                break;
            case PROCESS_EXCEPTION:
                RunLogger.getLogger().log(Level.WARNING, "process exception", throwable);
                session.close();
                break;
//            case INPUT_SHUTDOWN:
//                LOGGER.error("inputShutdown", throwable);
//                break;
//            case OUTPUT_EXCEPTION:
//                LOGGER.error("", throwable);
//                break;
//            case INPUT_EXCEPTION:
//                LOGGER.error("",throwable);
//                break;
//            case SESSION_CLOSED:
//                System.out.println("closeSession");
//                LOGGER.info("connection closed:{}");
//                break;
            case DECODE_EXCEPTION:
                throwable.printStackTrace();
                break;
//                default:
//                    System.out.println(stateMachineEnum);
        }
    }

    public Pipeline<HttpRequest, HttpResponse> pipeline(HttpHandle httpHandle) {
        return httpPipeline.next(httpHandle);
    }

    public Pipeline<HttpRequest, HttpResponse> pipeline() {
        return httpPipeline;
    }

    public Pipeline<WebSocketRequest, WebSocketResponse> wsPipeline(WebSocketHandle httpHandle) {
        return wsPipeline.next(httpHandle);
    }

    public Pipeline<WebSocketRequest, WebSocketResponse> wsPipeline() {
        return wsPipeline;
    }

}
