package org.noear.solon.extend.dubbo3.integration;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.config.*;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author iYing
 */
public class PluginImpl implements Plugin {

    private final Map<Class<?>, Object> references = new ConcurrentHashMap<>();

    @Override
    public void start(AopContext context) {
        if (Solon.app().source().getAnnotation(EnableDubbo.class) == null) {
            return;
        }

        this.initialize();
        this.register(context);
        this.bootstrap();
    }

    private void initialize() {
        ApplicationConfig applicationConfig = new ApplicationConfig("dubbo-demo-api-provider");
        applicationConfig.setQosEnable(false);
        applicationConfig.setCompiler("jdk");
        Map<String, String> m = new HashMap<>(1);
        m.put("proxy", "jdk");
        applicationConfig.setParameters(m);

        DubboBootstrap.getInstance()
                .application(new ApplicationConfig("dubbo-demo-triple-api-provider"))
                .registry(new RegistryConfig("zookeeper://127.0.0.1:2181"))
                .protocol(new ProtocolConfig(CommonConstants.TRIPLE, -1));
    }

    private void register(AopContext context) {
        context.beanBuilderAdd(DubboService.class, ((clazz, beanWrap, annotation) -> {
            Class<?>[] interfaces = beanWrap.clz()
                    .getInterfaces();

            if (interfaces.length > 0) {
                ServiceConfig<?> config = new ServiceConfig<>();
                config.setInterface(clazz);
                config.setRef(beanWrap.get());

                DubboBootstrap.getInstance()
                        .service(config);
            }
        }));

        context.beanInjectorAdd(DubboReference.class, ((holder, annotation) -> {
            if (holder.getType().isInterface()) {
                Object proxy = this.references.computeIfAbsent(holder.getType(), mapping -> {
                    ReferenceConfig<?> config = new ReferenceConfig<>();
                    config.setInterface(holder.getType().getName());
                    config.setCheck(annotation.check());
                    config.setProtocol(annotation.protocol());
                    config.setLazy(annotation.lazy());
                    config.setTimeout(annotation.timeout());

                    DubboBootstrap.getInstance()
                            .reference(config);

                    return config.get();
                });

                holder.setValue(proxy);
            }
        }));
    }

    private void bootstrap() {
        DubboBootstrap.getInstance()
                .start();
    }

    @Override
    public void stop() {
        boolean already = DubboBootstrap.getInstance().isStopped() && DubboBootstrap.getInstance().isStopping();
        if (!already) {
            DubboBootstrap.getInstance()
                    .stop();
        }
    }

}
