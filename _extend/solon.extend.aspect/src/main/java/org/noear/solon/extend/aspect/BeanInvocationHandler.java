package org.noear.solon.extend.aspect;

import org.noear.solon.core.wrap.MethodWrap;
import org.noear.solon.extend.aspect.asm.AsmProxy;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Bean 调用处理
 *
 * @author noear
 * @since 1.5
 * */
public class BeanInvocationHandler implements InvocationHandler {
    private Object bean;
    private Object proxy;
    private InvocationHandler handler;

    public BeanInvocationHandler(Object bean) {
        this(bean.getClass(), bean);
    }

    public BeanInvocationHandler(Class<?> clazz, Object bean) {
        this(clazz, bean, null);
    }

    /**
     * @since 1.6
     * */
    public BeanInvocationHandler(Object bean, InvocationHandler handler) {
        this(bean.getClass(), bean, handler);
    }

    /**
     * @since 1.6
     * */
    public BeanInvocationHandler(Class<?> clazz, Object bean, InvocationHandler handler) {
        try {
            Constructor constructor = clazz.getConstructor(new Class[]{});
            Object[] constructorParam = new Object[]{};

            this.handler = handler;
            this.bean = bean;
            this.proxy = AsmProxy.newProxyInstance(this, clazz, constructor, constructorParam);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    public Object getProxy() {
        return proxy;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (handler == null) {
            method.setAccessible(true);

            Object result = MethodWrap.get(method).invokeByAspect(bean, args);

            return result;
        } else {
            return handler.invoke(bean, method, args);
        }
    }
}
