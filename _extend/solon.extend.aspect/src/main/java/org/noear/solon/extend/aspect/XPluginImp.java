package org.noear.solon.extend.aspect;

import org.noear.solon.XApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XPlugin;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        Aop.factory().beanCreatorAdd(XAspect.class, (clz, wrap, anno) -> {
            wrap.proxySet(BeanProxyImp.global());
        });
    }
}
