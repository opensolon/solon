package org.noear.solon.extend.uapi;

import org.noear.solon.annotation.XMapping;
import org.noear.solon.core.BeanWebWrap;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.XAction;

import java.lang.reflect.Method;

public class ExBeanWebWrap extends BeanWebWrap {
    private UapiGateway gateway;

    public ExBeanWebWrap(BeanWrap wrap, String mapping, boolean remoting, UapiGateway gateway) {
        super(wrap, mapping, remoting);
        this.gateway = gateway;
    }

    @Override
    protected XAction action(BeanWrap bw, Method method, XMapping mp, String path) {
        return new ExXAction(bw, method, mp, path, c_remoting, gateway);
    }
}
