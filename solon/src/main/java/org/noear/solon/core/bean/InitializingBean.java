package org.noear.solon.core.bean;

/**
 * 可初始化的 Bean
 *
 * @author noear
 * @since 2.2
 * @deprecated 2.5
 */
@Deprecated
public interface InitializingBean {
    /**
     * 注入之后
     * */
    default void afterInjection() throws Throwable{}
}
