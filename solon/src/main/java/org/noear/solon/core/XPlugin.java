package org.noear.solon.core;

import org.noear.solon.XApp;

/**
 * 通用插件接口
 * */
@FunctionalInterface
public interface XPlugin {
    /**
     * 启动 （stop 可通过: app.onStop(..) 实现）
     */
    void start(XApp app);

    /**
     * 停止
     * */
    default void stop() throws Throwable{}
}
