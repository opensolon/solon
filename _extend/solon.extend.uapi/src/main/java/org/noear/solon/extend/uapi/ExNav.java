package org.noear.solon.extend.uapi;

import org.noear.solon.XNav;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XHandler;

/**
 * 为了拦截 handleDo 处理，并转发给网关
 * */
public class ExNav extends XNav {
    private UapiGateway gateway;

    public ExNav(XMapping mapping, UapiGateway gateway) {
        super(mapping);
        this.gateway = gateway;
    }

    @Override
    protected XHandler findDo(XContext c, String path) {
        return gateway.findDo(c, path);
    }

    @Override
    protected void handleDo(XContext c, XHandler h, int endpoint) throws Throwable {
        gateway.handleDo(c, h, endpoint);
    }
}
