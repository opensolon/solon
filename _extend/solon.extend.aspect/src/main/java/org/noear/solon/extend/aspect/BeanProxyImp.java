package org.noear.solon.extend.aspect;


import org.noear.solon.core.BeanWrap;

public class BeanProxyImp implements BeanWrap.Proxy {
    private static BeanWrap.Proxy _global;

    public static BeanWrap.Proxy global() {
        if (_global == null) {
            _global = new BeanProxyImp();
        }

        return _global;
    }

    @Override
    public Object getProxy(Object bean) {
        return new BeanInvocationHandler(bean).getProxy();
    }
}
