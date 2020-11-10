package org.noear.solon.extend.xsocket;

import org.noear.solon.XUtil;
import org.noear.solon.core.XMessage;
import org.noear.solon.core.XSession;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public abstract class XSessionBase implements XSession {
    @Override
    public XMessage sendAndResponse(XMessage message) {
        if (XUtil.isEmpty(message.key())) {
            throw new IllegalArgumentException("SendAndResponse message no key");
        }

        //注册请求
        CompletableFuture<XMessage> request = new CompletableFuture<>();
        XListenerProxy.regRequest(message, request);

        send(message);

        try {
            return request.get(10, TimeUnit.SECONDS);
        } catch (Throwable ex) {
            throw XUtil.throwableWrap(ex);
        }
    }
}
