package org.noear.solon.extend.xsocket;

import org.noear.solon.Utils;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.MessageSession;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public abstract class XSessionBase implements MessageSession {
    /**
     * 用于支持双向RPC
     * */
    @Override
    public Message sendAndResponse(Message message) {
        if (Utils.isEmpty(message.key())) {
            throw new IllegalArgumentException("SendAndResponse message no key");
        }

        //注册请求
        CompletableFuture<Message> request = new CompletableFuture<>();
        XListenerProxy.regRequest(message, request);

        //发送消息
        send(message);

        try {
            //等待响应
            return request.get(XListenerProxy.REQUEST_AND_RESPONSE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (Throwable ex) {
            throw Utils.throwableWrap(ex);
        }
    }
}
