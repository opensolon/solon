package org.noear.solon.extend.aspect;


import org.noear.solon.core.Aop;
import org.noear.solon.core.BeanWrap;

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
     * */
    public static BeanWrap.Proxy global() {
        if (_global == null) {
            _global = new BeanProxy();
        }

        return _global;
    }

    /**
     * 绑定
     * */
    public static boolean binding(BeanWrap bw) {
        if (bw.proxy() instanceof BeanProxy) {
            return false;
        } else {
            bw.proxySet(BeanProxy.global());
            Aop.context().beanRegister(bw, "", true);
            return true;
        }
    }

    @Override
    public Object getProxy(Object bean) {
        return new BeanInvocationHandler(bean).getProxy();
    }
}
