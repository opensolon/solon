package org.noear.solon.aspect;

import org.noear.solon.core.AopContext;
import org.noear.solon.aspect.asm.AsmProxy;

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
    private final AopContext context;

    /**
     * @since 1.6
     */
    public BeanInvocationHandler(AopContext ctx, Object bean, InvocationHandler handler) {
        this(ctx, bean.getClass(), bean, handler);
    }

    /**
     * @since 1.6
     */
    public BeanInvocationHandler(AopContext ctx, Class<?> clazz, Object bean, InvocationHandler handler) {
        try {
            Constructor constructor = clazz.getConstructor(new Class[]{});
            Object[] constructorParam = new Object[]{};

            this.context = ctx;
            this.handler = handler;
            this.bean = bean;
            this.proxy = AsmProxy.newProxyInstance(context,this, clazz, constructor, constructorParam);
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

            Object result = context.methodGet(method).invokeByAspect(bean, args);

            return result;
        } else {
            return handler.invoke(bean, method, args);
        }
    }
}
