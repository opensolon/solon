package org.noear.solon.extend.uapi;

import org.noear.solon.annotation.XMapping;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.XAction;
import org.noear.solon.core.XContext;

import java.lang.reflect.Method;

/**
 * 为了重写渲染动作
 * */
public class ExAction extends XAction {
    private UapiGateway gateway;

    public ExAction(BeanWrap bw, Method m, XMapping mp, String path, boolean remoting, UapiGateway gateway) {
        super(bw, m, mp, path, remoting);
        this.gateway = gateway;
    }

    @Override
    protected void renderDo(XContext x, Object result) throws Throwable {
        gateway.render(result, x);
    }
}
