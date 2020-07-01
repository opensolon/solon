package org.noear.solon.core;

/**
 * 监听器（内部类，外部不要使用）
 * */
public class XMonitor {
    private static XEventHandler<Throwable> errorEventHandler;

    public static void sendError(XContext ctx, Throwable err) {
        if (errorEventHandler != null) {
            try {
                errorEventHandler.handle(ctx, err);
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }
    }
    public static void onError(XEventHandler<Throwable> handler) {
        errorEventHandler = handler;
    }
}
