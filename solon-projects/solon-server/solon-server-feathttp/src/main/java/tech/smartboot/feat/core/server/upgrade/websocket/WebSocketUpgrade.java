/*
 *  Copyright (C) [2024] smartboot [zhengjunweimail@163.com]
 *
 *  企业用户未经smartboot组织特别许可，需遵循Apache-2.0开源协议合理合法使用本项目。
 *
 *   Enterprise users are required to use this project reasonably
 *   and legally in accordance with the Apache-2.0 open source agreement
 *  without special permission from the smartboot organization.
 */

package tech.smartboot.feat.core.server.upgrade.websocket;

import io.github.smartboot.socket.timer.HashedWheelTimer;
import io.github.smartboot.socket.timer.TimerTask;
import tech.smartboot.feat.core.common.HeaderName;
import tech.smartboot.feat.core.common.HeaderValue;
import tech.smartboot.feat.core.common.HttpStatus;
import tech.smartboot.feat.core.common.codec.websocket.BasicFrameDecoder;
import tech.smartboot.feat.core.common.codec.websocket.CloseReason;
import tech.smartboot.feat.core.common.codec.websocket.Decoder;
import tech.smartboot.feat.core.common.codec.websocket.WebSocket;
import tech.smartboot.feat.core.common.logging.Logger;
import tech.smartboot.feat.core.common.logging.LoggerFactory;
import tech.smartboot.feat.core.server.HttpRequest;
import tech.smartboot.feat.core.server.HttpResponse;
import tech.smartboot.feat.core.server.WebSocketRequest;
import tech.smartboot.feat.core.server.WebSocketResponse;
import tech.smartboot.feat.core.server.impl.Upgrade;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
public class WebSocketUpgrade extends Upgrade {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketUpgrade.class);
    private static final String WEBSOCKET_13_ACCEPT_GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
    private static final Decoder basicFrameDecoder = new BasicFrameDecoder();
    private Decoder decoder = basicFrameDecoder;
    private WebSocketRequestImpl webSocketRequest;
    private WebSocketResponseImpl webSocketResponse;
    private TimerTask wsIdleTask;
    /**
     * 闲置超时时间，单位：毫秒，默认：2分钟
     */
    private final long idleTimeout;

    public WebSocketUpgrade() {
        this(120000);
    }

    public WebSocketUpgrade(long idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    @Override
    public final void init(HttpRequest req, HttpResponse response) throws IOException {
        webSocketRequest = new WebSocketRequestImpl();
        webSocketResponse = new WebSocketResponseImpl(response);
        String acceptSeed = request.getHeader(HeaderName.Sec_WebSocket_Key) + WEBSOCKET_13_ACCEPT_GUID;
        byte[] sha1 = sha1Encode(acceptSeed);
        String accept = Base64.getEncoder().encodeToString(sha1);
        response.setHttpStatus(HttpStatus.SWITCHING_PROTOCOLS);
        response.setHeader(HeaderName.UPGRADE, HeaderValue.Upgrade.WEBSOCKET);
        response.setHeader(HeaderName.CONNECTION, HeaderValue.Connection.UPGRADE);
        response.setHeader(HeaderName.Sec_WebSocket_Accept, accept);
        OutputStream outputStream = response.getOutputStream();
        outputStream.flush();

        if (idleTimeout > 0) {
            wsIdleTask = HashedWheelTimer.DEFAULT_TIMER.scheduleWithFixedDelay(() -> {
                LOGGER.debug("check wsIdle monitor");
                if (System.currentTimeMillis() - request.getLatestIo() > idleTimeout && webSocketRequest != null) {
                    LOGGER.debug("close ws connection by idle monitor");
                    webSocketResponse.close(CloseReason.UNEXPECTED_ERROR, "ws idle timeout");
                }
            }, idleTimeout, TimeUnit.MILLISECONDS);
        }
        onHandShake(webSocketRequest, webSocketResponse);
    }

    private static byte[] sha1Encode(String str) {
        if (str == null) {
            return null;
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
            return messageDigest.digest(str.getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public final void onBodyStream(ByteBuffer buffer) {
        decoder = decoder.decode(buffer, webSocketRequest);
        if (decoder != WebSocket.PAYLOAD_FINISH) {
            return;
        }
        decoder = basicFrameDecoder;
        try {
            CompletableFuture<Void> future = new CompletableFuture<>();
            handle(webSocketRequest, webSocketResponse, future);
            if (future.isDone()) {
                finishResponse(webSocketRequest);
            } else {
                Thread thread = Thread.currentThread();
                request.getAioSession().awaitRead();
                future.thenRun(() -> {
                    try {
                        finishResponse(webSocketRequest);
                        if (thread != Thread.currentThread()) {
                            request.getAioSession().writeBuffer().flush();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        webSocketResponse.close(CloseReason.GOING_AWAY, "io exception");
                    } finally {
                        request.getAioSession().signalRead();
                    }
                });
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        if (buffer.hasRemaining()) {
            onBodyStream(buffer);
        }
    }

    /**
     * 执行当前处理器逻辑。
     * <p>
     * 当前handle运行完后若还有后续的处理器，需要调用doNext
     * </p>
     *
     * @param request
     * @param response
     * @throws IOException
     */
    public void handle(WebSocketRequest request, WebSocketResponse response) throws Throwable {
        try {
            switch (request.getFrameOpcode()) {
                case WebSocket.OPCODE_TEXT:
                    handleTextMessage(request, response, new String(request.getPayload(), StandardCharsets.UTF_8));
                    break;
                case WebSocket.OPCODE_BINARY:
                    handleBinaryMessage(request, response, request.getPayload());
                    break;
                case WebSocket.OPCODE_CLOSE:
                    try {
                        onClose(request, response, new CloseReason(request.getPayload()));
                    } finally {
                        response.close();
                    }
                    break;
                case WebSocket.OPCODE_PING:
                    handlePing(request, response);
                    break;
                case WebSocket.OPCODE_PONG:
                    handlePong(request, response);
                    break;
                case WebSocket.OPCODE_CONTINUE:
                    handleContinueMessage(request, response, request.getPayload());
                    break;
                default:
                    throw new UnsupportedOperationException();
            }
        } catch (Throwable throwable) {
            onError(request, throwable);
        }
    }

    public void handle(WebSocketRequest request, WebSocketResponse response, CompletableFuture<Void> completableFuture) throws Throwable {
        try {
            handle(request, response);
        } finally {
            completableFuture.complete(null);
        }
    }

    private void finishResponse(WebSocketRequestImpl abstractRequest) throws IOException {
        abstractRequest.reset();
    }

    public void handlePing(WebSocketRequest request, WebSocketResponse response) {
        response.pong(request.getPayload());
    }

    public void handlePong(WebSocketRequest request, WebSocketResponse response) {
        LOGGER.warn("receive pong...");
    }

    /**
     * 握手成功
     *
     * @param request
     * @param response
     */
    public void onHandShake(WebSocketRequest request, WebSocketResponse response) {
        LOGGER.warn("handShake success");
    }

    /**
     * 连接关闭
     *
     * @param request
     * @param response
     */
    public void onClose(WebSocketRequest request, WebSocketResponse response, CloseReason closeReason) {
        LOGGER.warn("close connection");
    }

    /**
     * 处理字符串请求消息
     *
     * @param request
     * @param response
     * @param message
     */
    public void handleTextMessage(WebSocketRequest request, WebSocketResponse response, String message) {
        System.out.println(message);
    }

    /**
     * 处理二进制请求消息
     *
     * @param request
     * @param response
     * @param data
     */
    public void handleBinaryMessage(WebSocketRequest request, WebSocketResponse response, byte[] data) {
        System.out.println(data);
    }

    public void handleContinueMessage(WebSocketRequest request, WebSocketResponse response, byte[] data) {
        LOGGER.warn("unSupport OPCODE_CONTINUE now,ignore payload: {}", data);
    }

    /**
     * 连接异常
     *
     * @param request
     * @param throwable
     */
    public void onError(WebSocketRequest request, Throwable throwable) throws Throwable {
        throw throwable;
    }

    @Override
    public void destroy() {
        if (wsIdleTask != null) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("cancel websocket idle monitor, request:{}", this);
            }
            wsIdleTask.cancel();
            wsIdleTask = null;
        }
    }
}
