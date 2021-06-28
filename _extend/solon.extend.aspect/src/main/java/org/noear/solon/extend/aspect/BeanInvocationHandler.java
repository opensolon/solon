package org.noear.solon.extend.aspect;

import org.noear.solon.core.wrap.MethodWrap;
import org.noear.solon.extend.aspect.asm.AsmProxy;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class BeanInvocationHandler implements InvocationHandler {
    private Object bean;
    private Object proxy;

    public BeanInvocationHandler(Object bean) {
        this(bean.getClass(), bean);
    }

    public BeanInvocationHandler(Class<?> clazz, Object bean) {
        try {
            Constructor constructor = clazz.getConstructor(new Class[]{});
            Object[] constructorParam = new Object[]{};

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
        method.setAccessible(true);

        Object result = MethodWrap.get(method).invokeByAspect(bean, args);

        return result;
    }
}
