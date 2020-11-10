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
            throw new RuntimeException("SendAndResponse message no key!");
        }

        CompletableFuture<XMessage> future = new CompletableFuture<>();
        XListenerProxy.addFuture(message,future);

        send(message);

        try {
            return future.get(10, TimeUnit.SECONDS);
        } catch (Throwable ex) {
            throw XUtil.throwableWrap(ex);
        }
    }
}
