package org.noear.solon.extend.dubbo.integration;

import org.apache.dubbo.config.*;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;
import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.extend.dubbo.EnableDubbo;


public class XPluginImp implements Plugin {
    private DubboBootstrap bootstrap;

    @Override
    public void start(AopContext context) {
        if (Solon.app().source().getAnnotation(EnableDubbo.class) == null) {
            return;
        }

        bootstrap = DubboBootstrap.getInstance();

        this.initialize();
        this.register(context);

        bootstrap.start();
    }

    private void initialize() {
        // 应用配置
        ApplicationConfig application = Solon.cfg()
                .getBean("dubbo.application", ApplicationConfig.class);
        // 如果没有设置应用名称则注入进去
        if (application.getName() == null) {
            application.setName(Solon.cfg().appGroup() + "-" + Solon.cfg().appName());
        }

        // 注册中心
        RegistryConfig registry = Solon.cfg()
                .getBean("dubbo.registry", RegistryConfig.class);

        // 协议
        ProtocolConfig protocol = Solon.cfg()
                .getBean("dubbo.protocol", ProtocolConfig.class);

        bootstrap.application(application)
                .registry(registry)
                .protocol(protocol);
    }

    private void register(AopContext context) {
        context.beanBuilderAdd(DubboService.class, ((clz, bw, anno) -> {
            Class<?>[] interfaces = clz.getInterfaces();

            if (interfaces.length > 0) {
                ServiceConfig<?> config = new ServiceConfig<>(new DubboServiceAnno(anno));
                config.setInterface(interfaces[0]);
                config.setRef(bw.get());

                bootstrap.service(config);
            }
        }));

        context.beanInjectorAdd(DubboReference.class, ((holder, anno) -> {
            if (holder.getType().isInterface()) {
                ReferenceConfig<?> config = new ReferenceConfig<>(new DubboReferenceAnno(anno));

                // 注册引用
                bootstrap.reference(config);

                holder.setValue(config.get());
            }
        }));

        //兼容旧的


        context.beanBuilderAdd(Service.class, ((clz, bw, anno) -> {
            Class<?>[] interfaces = clz.getInterfaces();

            if (interfaces.length > 0) {
                ServiceConfig<?> config = new ServiceConfig<>(new ServiceAnno(anno));
                config.setInterface(interfaces[0]);
                config.setRef(bw.get());

                bootstrap.service(config);
            }
        }));

        context.beanInjectorAdd(Reference.class, ((holder, anno) -> {
            if (holder.getType().isInterface()) {
                ReferenceConfig<?> config = new ReferenceConfig<>(new ReferenceAnno(anno));

                // 注册引用
                bootstrap.reference(config);

                holder.setValue(config.get());
            }
        }));
    }

    @Override
    public void stop() {
        if (bootstrap != null) {
            bootstrap.stop();
            bootstrap = null;
        }
    }
}
