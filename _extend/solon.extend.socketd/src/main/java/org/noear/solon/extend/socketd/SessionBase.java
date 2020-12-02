package org.noear.solon.extend.socketd;

import org.noear.solon.Utils;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class SessionBase implements Session {
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
}
