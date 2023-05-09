package org.noear.solon.proxy;

import org.noear.solon.Solon;
import org.noear.solon.core.runtime.NativeDetector;
import org.noear.solon.core.util.LogUtil;
import org.noear.solon.proxy.aot.AotProxy;
import org.noear.solon.core.AopContext;
import org.noear.solon.proxy.asm.AsmProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Bean 调用处理
 *
 * @author noear
 * @since 1.5
 * */
public class BeanInvocationHandler implements InvocationHandler {
    private Object target;
    private Object proxy;
    private InvocationHandler handler;
    private final AopContext context;

    /**
     * @since 1.6
     */
    public BeanInvocationHandler(AopContext context, Object bean, InvocationHandler handler) {
        this(context, bean.getClass(), bean, handler);
    }

    /**
     * @since 1.6
     * @since 2.1
     */
    public BeanInvocationHandler(AopContext context, Class<?> clazz, Object target, InvocationHandler handler) {
        this.context = context;
        this.target = target;
        this.handler = handler;

        //支持 AOT 生成的代理 (支持 Graalvm Native  打包)
        if (NativeDetector.isAotRuntime() == false) {
            this.proxy = AotProxy.newProxyInstance(context, this, clazz);
        }

        if (this.proxy == null) {
            //支持 ASM（兼容旧的包，不支持 Graalvm Native  打包）
            this.proxy = AsmProxy.newProxyInstance(context, this, clazz);
        }

        //调试时打印信息
        if (Solon.cfg().isDebugMode()) {
            if (this.proxy != null) {
                LogUtil.global().trace("proxy class:" + this.proxy.getClass().getName());
            }
        }
    }

    public Object getProxy() {
        return proxy;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (handler == null) {
            method.setAccessible(true);

            Object result = context.methodGet(method).invokeByAspect(target, args);

            return result;
        } else {
            return handler.invoke(target, method, args);
        }
    }
}
