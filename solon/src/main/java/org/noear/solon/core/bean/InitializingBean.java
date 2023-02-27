package org.noear.solon.core.bean;

/**
 * 可初始化Bean
 *
 * @author noear
 * @since 2.2
 */
public interface InitializingBean {
    void afterPropertiesSet() throws Throwable;
}
