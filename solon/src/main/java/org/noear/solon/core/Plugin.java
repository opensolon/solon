package org.noear.solon.core;

/**
 * 通用插件接口（实现 Plugin 架构；通过Solon SPI进行申明）
 *
 * @author noear
 * @since 1.0
 * */
public interface Plugin {
    /**
     * 启动（保留，为兼容性过度）
     *
     * @deprecated 2.5
     */
    @Deprecated
    default void start(AopContext context) throws Throwable{}

    /**
     * 启动
     * */
    default void start(AppContext context) throws Throwable{
        start((AopContext)context);
    }

    /**
     * 预停止
     * */
    default void prestop() throws Throwable{}

    /**
     * 停止
     * */
    default void stop() throws Throwable{}
}
