package org.noear.solon.core;

public class XBridge {
    public static XUpstreamFactory upstreamFactory = (service -> {throw new RuntimeException("Uninitialized upstreamFactory!");});

    static {
        Aop.getAsyn(XUpstreamFactory.class, (bw) -> {
            upstreamFactory = bw.get();
        });
    }
}
