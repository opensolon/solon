package com.dtflys.forest.reflection;

import org.noear.solon.core.AopContext;

/**
 * @author noear
 * @since 1.11
 */
public class SolonObjectFactory extends DefaultObjectFactory{
    final AopContext context;
    public SolonObjectFactory(AopContext context){
        this.context = context;
    }

    /**
     * 获取Forest接口对象(Spring方式)
     * <p>适用于Forest相关接口(非请求客户端接口)和回调函数的工厂接口
     * <p>当这些类没有实例的情况下，会先实例化并缓存下来，以后再取会通过缓存获取对象
     * <p>实例化方式：通过Spring上下文获取Bean
     *
     * @param clazz Forest对象接口类
     * @param <T> Forest对象接口类泛型
     * @return Forest对象实例
     */
    @Override
    public <T> T getObject(Class<T> clazz) {
        T bean = getObjectFromCache(clazz);

        if (bean == null) {
            try {
                bean = context.getBeanOrNew(clazz);
            } catch (Throwable ignored) {
            }

            if (bean == null) {
                bean = super.getObject(clazz);
            }
        }
        return bean;
    }
}
