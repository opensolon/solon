package org.noear.solon.core;

/**
 * Bean 代理接口（为BeanWrap 提供切换代码的能力）
 *
 * @author noear
 * @since 1.0
 * */
@FunctionalInterface
public interface BeanProxy {
    /**
     * 获取代理
     * */
    Object getProxy(Object bean);
}
