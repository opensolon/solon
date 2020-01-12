package org.noear.solon.extend.dubbo;

import org.noear.solon.XApp;
import org.noear.solon.core.XPlugin;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        ClassPathXmlApplicationContext cpxac = new ClassPathXmlApplicationContext("dubbo_provider.xml");
        cpxac.start();
    }


    @Override
    public void stop() throws Throwable {
    }
}
