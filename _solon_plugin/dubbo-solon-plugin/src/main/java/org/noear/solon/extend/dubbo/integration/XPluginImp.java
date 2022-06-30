package org.noear.solon.extend.dubbo.integration;

import org.apache.dubbo.config.ServiceConfig;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.extend.dubbo.EnableDubbo;


public class XPluginImp implements Plugin {

    @Override
    public void start(AopContext context) {
        if (Solon.app().source().getAnnotation(EnableDubbo.class) == null) {
            return;
        }

        DubboManager _manager = DubboManager.global();

        //支持duboo.Service注解
        context.beanBuilderAdd(Service.class, ((clz, bw, anno) -> {
            Class<?>[] ifs = bw.clz().getInterfaces();
            if (ifs.length > 0) {
                ServiceConfig cfg = new ServiceConfig(new ServiceAnno(anno));
                if (cfg.getInterface() == null) {
                    cfg.setInterface(ifs[0]);
                }
                cfg.setRef(bw.raw());

                // 暴露及注册服务
                _manager.regService(cfg);
            }
        }));

        //支持dubbo.Reference注入
        context.beanInjectorAdd(Reference.class, ((fwT, anno) -> {
            if (fwT.getType().isInterface()) {
                Object raw = _manager.getService(fwT.getType(), new ReferenceAnno(anno));
                fwT.setValue(raw);
            }
        }));
    }
}
