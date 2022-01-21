package org.noear.solon.extend.aspect;

import org.noear.solon.core.Aop;
import org.noear.solon.core.BeanWrap;

import java.lang.reflect.InvocationHandler;

/**
 * Bean 动态代理
 *
 * @author noear
 * @since 1.5
 * */
public class BeanProxy implements BeanWrap.Proxy {
    private static BeanWrap.Proxy _global;

    /**
     * 全局实例
     */
    public static BeanWrap.Proxy global() {
        if (_global == null) {
            _global = new BeanProxy();
        }

        return _global;
    }

    /**
     * 绑定
     *
     * @param name  注册名字
     * @param typed 注册类型（当 name 不为空时才有效；否则都算 true）
     */
    public static boolean binding(BeanWrap bw, String name, boolean typed) {
        if (bw.proxy() instanceof BeanProxy) {
            return false;
        } else {
            bw.proxySet(BeanProxy.global());
            Aop.context().beanRegister(bw, name, typed);
            return true;
        }
    }

    /**
     * 绑定
     */
    public static boolean binding(BeanWrap bw) {
        return binding(bw, "", false);
    }


    /**
     * 系上，并转发给 handler
     *
     * @since 1.6
     */
    public static <T> T attach(T bean, InvocationHandler handler) {
        return (T) new BeanInvocationHandler(bean, handler).getProxy();
    }

    /**
     * 获取一个代理
     */
    @Override
    public Object getProxy(Object bean) {
        return new BeanInvocationHandler(bean).getProxy();
    }
}
