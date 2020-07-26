package org.noear.solon.extend.uapi;

import org.noear.solon.annotation.XMapping;
import org.noear.solon.core.BeanWebWrap;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.XAction;

import java.lang.reflect.Method;

/**
 * 为了获取 创建 XAction 的权限
 * */
public class ExBeanWebWrap extends BeanWebWrap {
    private UapiGateway gateway;

    public ExBeanWebWrap(BeanWrap wrap, String mapping, boolean remoting, UapiGateway gateway) {
        super(wrap, mapping, remoting);
        this.gateway = gateway;
    }

    @Override
    protected XAction action(BeanWrap bw, Method method, XMapping mp, String path) {
        if (gateway.allowActionMapping()) {
            return new ExAction(bw, method, mp, path, c_remoting, gateway);
        } else {
            return new ExAction(bw, method, null, path, c_remoting, gateway);
        }
    }
}
