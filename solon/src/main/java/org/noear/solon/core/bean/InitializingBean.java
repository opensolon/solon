package org.noear.solon.core.bean;

/**
 * 可初始化Bean
 *
 * @author noear
 * @since 2.2
 */
public interface InitializingBean {
    /**
     * 属性注入后
     * */
    default void afterPropertiesSet() throws Throwable{}
}
