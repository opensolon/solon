package org.noear.solon.extend.aspect;

import org.noear.solon.core.BeanProxy;

public class BeanProxyImp implements BeanProxy {
    private static BeanProxy _global;

    public static BeanProxy global() {
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
