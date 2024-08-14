package org.noear.solon.core.bean;

/**
 * 带容器生命周期的 Bean
 *
 * @author noear
 * @since 2.9
 */
public interface LifecycleSimpleBean extends LifecycleBean {
    @Override
    default void start() throws Throwable {

    }
}
