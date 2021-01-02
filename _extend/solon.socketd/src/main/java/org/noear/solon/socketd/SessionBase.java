package org.noear.solon.socketd;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.message.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

public abstract class SessionBase implements Session {
    private int _flag;
    @Override
    public int flag() {
        return _flag;
    }

    @Override
    public void flagSet(int flag) {
        _flag = flag;
    }

    //////////////////////////////////////////

    private AtomicBoolean _handshaked = new AtomicBoolean();

    /**
     * 设置握手状态
     */
    public void setHandshaked(boolean handshaked) {
        _handshaked.set(handshaked);
    }

    /**
     * 获取握手状态
     */
    public boolean getHandshaked() {
        return _handshaked.get();
    }

    //////////////////////////////////////////


    @Override
    public void send(Message message) {
        if (Solon.cfg().isFilesMode() || Solon.cfg().isDebugMode()) {
            System.out.println("Session send: " + message);
        }
    }

    @Override
    public String sendAndResponse(String message) {
        return sendAndResponse(Message.wrap(message)).bodyAsString();
    }

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

    @Override
    public void sendAndCallback(String message, BiConsumer<String, Throwable> callback) {
        sendAndCallback(Message.wrap(message), (msg, err) -> {
            if (msg == null) {
                callback.accept(null, err);
            } else {
                callback.accept(msg.bodyAsString(), err);
            }
        });
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

    protected void onOpen() {
        if (listener() != null) {
            listener().onOpen(this);
        }
    }


    /**
     * 发送心跳
     */
    @Override
    public void sendHeartbeat() {
        send(Message.wrapHeartbeat());
    }

    private boolean _sendHeartbeatAuto = false;

    @Override
    public void sendHeartbeatAuto(int intervalSeconds) {
        if (_sendHeartbeatAuto) {
            return;
        }

        _sendHeartbeatAuto = true;

        Utils.scheduled.scheduleWithFixedDelay(
                this::sendHeartbeat, 1, intervalSeconds, TimeUnit.SECONDS);
    }

    //保存最后一次握手的信息；之后重链时使用
    protected Message handshakeMessage;

    @Override
    public void sendHandshake(Message message) {
        if (message.flag() == MessageFlag.handshake) {
            send(message);

            //发完之后，再缓存 //不然，会发两次
            handshakeMessage = message;
        } else {
            throw new IllegalArgumentException("The message flag not handshake");
        }
    }

    @Override
    public Message sendHandshakeAndResponse(Message message) {
        if (message.flag() == MessageFlag.handshake) {
            Message rst = sendAndResponse(message);

            //发完之后，再缓存 //不然，会发两次
            handshakeMessage = message;

            return rst;
        } else {
            throw new IllegalArgumentException("The message flag not handshake");
        }
    }
}