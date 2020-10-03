package org.noear.solon.extend.socketapi;

import org.noear.solon.XApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XPlugin;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        Aop.factory().beanCreatorAdd(XSignalEndpoint.class, (clz, wrap, anno) -> {
            if (XListener.class.isAssignableFrom(clz)) {
                XListenerProxy.getInstance().add(anno.value(), wrap);
            }
        });
    }
}
