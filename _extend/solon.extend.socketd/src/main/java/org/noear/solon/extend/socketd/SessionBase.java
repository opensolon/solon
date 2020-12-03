package org.noear.solon.extend.socketd;

import org.noear.solon.Utils;
import org.noear.solon.core.message.Listener;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public abstract class SessionBase implements Session {
    //////////////////////////////////////////

    private boolean _handshaked;

    /**
     * 设置握手状态
     */
    public void setHandshaked(boolean handshaked) {
        _handshaked = handshaked;
    }

    /**
     * 获取握手状态
     */
    public boolean getHandshaked() {
        return _handshaked;
    }

    //////////////////////////////////////////

    /**
     * 用于支持双向RPC
     */
    @Override
    public Message sendAndResponse(Message message) {
        if (Utils.isEmpty(message.key())) {
            throw new IllegalArgumentException("SendAndResponse message no key");
        }

        //注册请求
        CompletableFuture<Message> request = new CompletableFuture<>();
        ListenerProxy.regRequest(message, request);

        //发送消息
        send(message);

        try {
            //等待响应
            return request.get(ListenerProxy.REQUEST_AND_RESPONSE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (Throwable ex) {
            throw Utils.throwableWrap(ex);
        }
    }

    /**
     * 用于支持异步回调
     */
    @Override
    public void sendAndCallback(Message message, BiConsumer<Message, Throwable> callback) {
        if (Utils.isEmpty(message.key())) {
            throw new IllegalArgumentException("sendAndCallback message no key");
        }

        //注册请求
        CompletableFuture<Message> request = new CompletableFuture<>();
        ListenerProxy.regRequest(message, request);

        //等待响应
        request.whenCompleteAsync(callback);

        //发送消息
        send(message);
    }

    private Listener listener;

    /**
     * 当前实例监听者（ListenEndpoint 为路径监听者，不限实例）
     */
    @Override
    public Listener listener() {
        return listener;
    }

    @Override
    public void listener(Listener listener) {
        this.listener = listener;
    }


    /**
     * 发送心跳
     */
    @Override
    public void sendHeartbeat() {
        send(MessageWrapper.wrapHeartbeat());
    }

    private boolean _sendHeartbeatAuto = false;

    @Override
    public void sendHeartbeatAuto(int intervalSeconds) {
        if (_sendHeartbeatAuto) {
            return;
        }

        _sendHeartbeatAuto = true;

        Utils.scheduled.scheduleWithFixedDelay(
                this::sendHeartbeat, 0, intervalSeconds, TimeUnit.SECONDS);
    }

    //保存最后一次握手的信息；之后重链时使用
    protected Message handshakeMessage;

    @Override
    public void sendHandshake(String header) {
        Message tmp = MessageWrapper.wrapHandshake(header);
        send(handshakeMessage);

        //发完之后，再缓存 //不然，会发两次
        handshakeMessage = tmp;
    }
}
