package org.noear.solon.core;

/**
 * Bean 代理接口
 *
 * @author noear
 * @since 1.0
 * */
@FunctionalInterface
public interface BeanProxy {
    Object getProxy(Object bean);
}
