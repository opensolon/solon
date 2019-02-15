package org.noear.solon.extend.wateradpter;

import org.noear.solon.XApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XPlugin;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        Aop.beanOnloaded(()->{
            if(XWaterAdapter.global()!=null) {
                Aop.beanForeach((k, v) -> {
                    if (k.startsWith("msg:") && XMessageHandler.class.isAssignableFrom(v.clz())) {
                        String msg = k.split(":")[1];

                        XWaterAdapter.global().router().put(msg, v.raw());
                    }
                });

                XWaterAdapter.global().messageSubscribeHandler();
            }
        });
    }
}
