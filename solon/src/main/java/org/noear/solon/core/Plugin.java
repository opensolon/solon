package org.noear.solon.core;

/**
 * 通用插件接口（实现 Plugin 架构；通过Solon ISP进行申明）
 *
 * @author noear
 * @since 1.0
 * */
@FunctionalInterface
public interface Plugin {
    /**
     * 初始化（不允许做Aop相关的事情）
     * */
    default void init(AopContext context) throws Throwable{}

    /**
     * 启动 （stop 可通过: app.onStop(..) 实现）
     */
    void start(AopContext context) throws Throwable;

    /**
     * 预停止
     * */
    default void prestop() throws Throwable{}

    /**
     * 停止
     * */
    default void stop() throws Throwable{}
}
