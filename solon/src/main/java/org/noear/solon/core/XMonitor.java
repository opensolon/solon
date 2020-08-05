package org.noear.solon.core;

import org.noear.solon.XApp;

/**
 * 监听器（内部类，外部不要使用）
 * */
public class XMonitor {
    private static XEventHandler<Throwable> errorEventHandler;

    /**
     * 发送异常
     * */
    public static void sendError(XContext ctx, Throwable err) {
        if (errorEventHandler != null) {
            try {
                errorEventHandler.handle(ctx, err);
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }else{
            if(XApp.cfg().isDebugMode()){
                err.printStackTrace();
            }
        }
    }

    /**
     * 添加异常处理器（只能一个）
     * */
    public static void onError(XEventHandler<Throwable> handler) {
        errorEventHandler = handler;
    }
}
