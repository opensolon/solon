package org.noear.solon.extend.dubbo;

import org.apache.dubbo.config.ServiceConfig;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.noear.solon.XApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XPlugin;


public class XPluginImp implements XPlugin {
    DubboAdapter _server;

    @Override
    public void start(XApp app) {
        boolean enableDubbo = true;
        if (app.source() != null) {
            //
            //Dubbo服务，默认是开启的；如果关掉，则不处理bubbo相关
            //
            EnableDubbo anno = app.source().getAnnotation(EnableDubbo.class);
            if (anno != null && anno.value() == false) {
                enableDubbo = false;
            }
        }

        _server = DubboAdapter.global();


        if(enableDubbo) {
            //支持XBean.remoting注解
            Aop.beanOnloaded(() -> {
                Aop.beanForeach((name, bw) -> {
                    if (bw.remoting()) {
                        if(bw.clz().getAnnotation(Service.class) != null){
                            //
                            //交给@Service处理
                            //
                            return;
                        }

                        Class<?>[] ifs = bw.clz().getInterfaces();
                        if (ifs.length == 1) {
                            ServiceConfig cfg = new ServiceConfig();
                            cfg.setInterface(ifs[0]);
                            cfg.setRef(bw.raw());

                            // 暴露及注册服务
                            _server.regService(cfg);
                        }
                    }
                });
            });

            //支持duboo.Service注解
            Aop.factory().beanCreatorAdd(Service.class, ((clz, bw, anno) -> {
                Class<?>[] ifs = bw.clz().getInterfaces();
                if (ifs.length > 0) {
                    ServiceConfig cfg = new ServiceConfig(anno);
                    if (cfg.getInterface() == null) {
                        cfg.setInterface(ifs[0]);
                    }
                    cfg.setRef(bw.raw());

                    // 暴露及注册服务
                    _server.regService(cfg);
                }
            }));
        }

        //支持dubbo.Reference注入
        Aop.factory().beanInjectorAdd(Reference.class, ((fwT, anno) -> {
            if (fwT.getType().isInterface()) {
                Object raw = DubboAdapter.global().getService(fwT.getType(), anno);
                fwT.setValue(raw);
            }
        }));
    }


    @Override
    public void stop() throws Throwable {
        if (_server == null) {
            return;
        }

        _server.stopBlock();
        _server = null;
    }
}
