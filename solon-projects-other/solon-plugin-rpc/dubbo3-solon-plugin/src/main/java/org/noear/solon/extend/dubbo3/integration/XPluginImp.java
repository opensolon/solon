package org.noear.solon.extend.dubbo3.integration;

import org.apache.dubbo.config.*;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;

import org.noear.solon.Solon;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.extend.dubbo3.EnableDubbo;

/**
 * @author iYing
 * @since 1.9
 */
public class XPluginImp implements Plugin {
    private DubboBootstrap bootstrap;

    @Override
    public void start(AppContext context){
        if (Solon.app().source().getAnnotation(EnableDubbo.class) == null) {
            return;
        }

        bootstrap = DubboBootstrap.getInstance();

        this.initialize();
        this.register(context);

        bootstrap.start();
        startBlock();
    }

    private void initialize() {
        // 应用配置
        ApplicationConfig application = Solon.cfg()
                .getBean("dubbo.application", ApplicationConfig.class);
        if (application == null) {
            application = new ApplicationConfig();
        }
        if (application.getName() == null) {
            application.setName(Solon.cfg().appGroup() + "-" + Solon.cfg().appName());
        }

        bootstrap.application(application);


        // 注册中心
        Registries registries = Solon.cfg()
                .getBean("dubbo.registries", Registries.class);
        if (registries != null && registries.size() > 0) {
            bootstrap.registries(registries);
        } else {
            RegistryConfig registry = Solon.cfg()
                    .getBean("dubbo.registry", RegistryConfig.class);
            if (registry == null) {
                registry = new RegistryConfig();
            }
            if (registry.getAddress() == null) {
                registry.setAddress("A/N");
            }

            bootstrap.registry(registry);
        }


        //提供者
        ProviderConfig provider = Solon.cfg()
                .getBean("dubbo.provider", ProviderConfig.class);
        if (provider != null) {
            bootstrap.provider(provider);
        }

        //协议
        Protocols protocols = Solon.cfg()
                .getBean("dubbo.protocols", Protocols.class);
        if (protocols != null && protocols.size() > 0) {
            bootstrap.protocols(protocols);
        } else {
            ProtocolConfig protocol = Solon.cfg()
                    .getBean("dubbo.protocol", ProtocolConfig.class);
            if (protocol == null) {
                protocol = new ProtocolConfig();
            }
            if (protocol.getName() == null) {
                protocol.setName("dubbo");
                int port = Solon.cfg().serverPort() + 20000;
                protocol.setPort(port);
            }


            bootstrap.protocol(protocol);
        }

        //消费者
        ConsumerConfig consumer = Solon.cfg()
                .getBean("dubbo.consumer", ConsumerConfig.class);
        if (consumer != null) {
            bootstrap.consumer(consumer);
        }
    }

    private void register(AppContext context) {
        context.beanBuilderAdd(DubboService.class, ((clz, bw, anno) -> {
            Class<?>[] interfaces = clz.getInterfaces();

            if (interfaces.length > 0) {
                ServiceConfig<?> config = new ServiceConfig<>(new DubboServiceAnno(anno));
                if (config.getInterface() == null) {
                    config.setInterface(interfaces[0]);
                }
                config.setRef(bw.get());
                config.export();

                bootstrap.service(config);
            }
        }));

        context.beanInjectorAdd(DubboReference.class, ((holder, anno) -> {
            if (holder.getType().isInterface()) {
                ReferenceConfig<?> config = new ReferenceConfig<>(new DubboReferenceAnno(anno));
                config.setInterface(holder.getType());

                // 注册引用
                bootstrap.reference(config);

                holder.setValue(config.get());
            }
        }));

        //todo:兼容旧的


        context.beanBuilderAdd(Service.class, ((clz, bw, anno) -> {
            Class<?>[] interfaces = clz.getInterfaces();

            if (interfaces.length > 0) {
                ServiceConfig<?> config = new ServiceConfig<>(new ServiceAnno(anno));
                if (config.getInterface() == null) {
                    config.setInterface(interfaces[0]);
                }
                config.setRef(bw.get());
                config.export();

                bootstrap.service(config);
            }
        }));

        context.beanInjectorAdd(Reference.class, ((holder, anno) -> {
            if (holder.getType().isInterface()) {
                ReferenceConfig<?> config = new ReferenceConfig<>(new ReferenceAnno(anno));
                config.setInterface(holder.getType());

                // 注册引用
                bootstrap.reference(config);

                holder.setValue(config.get());
            }
        }));
    }

    @Override
    public void prestop() throws Throwable {
        if (bootstrap != null) {
            bootstrap.stop();
            bootstrap = null;
        }
    }

    @Override
    public void stop() {
        stopBlock();
    }


    private Thread blockThread = null;

    public void startBlock() {
        if (blockThread == null) {
            blockThread = new Thread(() -> {
                try {
                    System.in.read();
                } catch (Exception ex) {
                }
            });
            blockThread.start();
        }
    }

    public void stopBlock() {
        if (blockThread != null) {
            blockThread.interrupt();
            blockThread = null;
        }
    }
}
