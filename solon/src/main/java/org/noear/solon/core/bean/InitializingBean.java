package org.noear.solon.core.bean;

/**
 * 可初始化的 Bean
 *
 * @author noear
 * @since 2.2
 */
public interface InitializingBean {
    /**
     * 字段注入后
     * */
    default void afterFieldsInject() throws Throwable{}
}
