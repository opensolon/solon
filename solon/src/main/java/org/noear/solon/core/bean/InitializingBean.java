package org.noear.solon.core.bean;

/**
 * 可初始化的 Bean
 *
 * @author noear
 * @since 2.2
 */
@FunctionalInterface
public interface InitializingBean {
    /**
     * 注入之后
     * */
    void afterInjection() throws Throwable;
}
