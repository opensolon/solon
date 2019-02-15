package org.noear.solon.extend.aopx;

import org.noear.solon.XApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XPlugin;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        Aop.factorySet(new AopFactoryEx());
    }
}
