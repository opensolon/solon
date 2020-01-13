package org.noear.solon.extend.dubbo;

import com.alibaba.dubbo.config.ServiceConfig;
import com.alibaba.dubbo.config.annotation.Service;
import org.noear.solon.XApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XPlugin;


public class XPluginImp implements XPlugin {
    DubboAdapter _server;

    @Override
    public void start(XApp app) {

        _server = DubboAdapter.global();

        // 服务提供者暴露服务配置
        Aop.beanOnloaded(() -> {
            Aop.beanForeach((name, bw) -> {
                if (bw.remoting()) {

                    Class<?>[] ifs = bw.clz().getInterfaces();
                    if (ifs.length == 1) {
                        Service sAnno = bw.clz().getAnnotation(Service.class);
                        ServiceConfig service = null;
                        if (sAnno == null) {
                            service = new ServiceConfig();
                            service.setVersion("1.0.0");
                        } else {
                            service = new ServiceConfig(sAnno);
                        }

                        service.setInterface(ifs[0]);
                        service.setRef(bw.raw());

                        // 暴露及注册服务
                        _server.regService(service);
                    }
                }
            });
        });
    }


    @Override
    public void stop() throws Throwable {
    }
}
