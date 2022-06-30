package org.noear.solon.extend.dubbo3.integration;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.ServiceConfig;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;

import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.extend.dubbo3.EnableDubbo;

/**
 * @author iYing
 * @since 1.9
 */
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
        Registries registries = Solon.cfg()
                .getBean("dubbo.registry", Registries.class);

        // 协议
        ProtocolConfig protocol = Solon.cfg()
                .getBean("dubbo.protocol", ProtocolConfig.class);

        bootstrap.application(application)
                .registries(registries)
                .protocol(protocol);
    }

    private void register(AopContext context) {
        context.beanBuilderAdd(DubboService.class, ((clazz, bw, anno) -> {
            Class<?>[] interfaces = bw.clz().getInterfaces();

            if (interfaces.length > 0) {
                ServiceConfig<?> config = new ServiceConfig<>(new DubboServiceAnno(anno));
                config.setInterface(clazz);
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
    }

    @Override
    public void stop() {
        if (bootstrap != null) {
            boolean already = bootstrap.isStopped() && bootstrap.isStopping();

            if (!already) {
                bootstrap.stop();
            }
        }
    }
}
