package org.noear.solon.core;

import org.noear.solon.ext.Act2;

/**
 * 监听器（内部类，外部不要使用）
 * */
public class XMonitor {
    private static Act2<XContext,Throwable> _onErrorEvent;

    public static void sendError(XContext ctx, Throwable err) {
        if (_onErrorEvent != null) {
            try {
                _onErrorEvent.run(ctx, err);
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }
    }
    public static void onError(Act2<XContext,Throwable> event) {
        _onErrorEvent = event;
    }
}
