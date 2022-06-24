package org.noear.solon.extend.dubbo3.integration;

import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.ServiceConfig;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.extend.dubbo3.EnableDubbo;
import org.noear.solon.extend.dubbo3.entities.Registries;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author iYing
 */
public class PluginImpl implements Plugin {

    public static final String CONFIGURATION_KEY_PREFIX = "dubbo";

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
        // 应用配置
        ApplicationConfig application = Solon.cfg()
                .getBean(String.format("%s.application", PluginImpl.CONFIGURATION_KEY_PREFIX), ApplicationConfig.class);
        // 如果没有设置应用名称则注入进去
        if (application.getName() == null) {
            application.setName(
                    Solon.cfg().appGroup() + "§" + Solon.cfg().appName()
            );
        }
        // 注册中心
        Registries registries = Solon.cfg()
                .getBean(String.format("%s.registries", PluginImpl.CONFIGURATION_KEY_PREFIX), Registries.class);
        // 协议
        ProtocolConfig protocol = Solon.cfg()
                .getBean(String.format("%s.protocol", PluginImpl.CONFIGURATION_KEY_PREFIX), ProtocolConfig.class);

        DubboBootstrap.getInstance()
                .application(application)
                .registries(registries)
                .protocol(protocol);
    }

    private void register(AopContext context) {
        context.beanBuilderAdd(DubboService.class, ((clazz, beanWrap, annotation) -> {
            Class<?>[] interfaces = beanWrap.clz()
                    .getInterfaces();

            if (interfaces.length > 0) {
                ServiceConfig<?> config = new ServiceConfig<>();
                config.setInterface(clazz);
                config.setRef(beanWrap.get());

                Properties properties = Solon.cfg()
                        .getProp("dubbo.services." + config.getInterface());
                if (properties.size() > 0) {
                    Utils.bindTo(properties, config);
                }

                DubboBootstrap.getInstance()
                        .service(config);
            }
        }));

        context.beanInjectorAdd(DubboReference.class, ((holder, annotation) -> {
            if (holder.getType().isInterface()) {
                Object proxy = this.references.computeIfAbsent(holder.getType(), mapping -> {
                    ReferenceConfig<?> config = new ReferenceConfig<>();
                    // 配置映射 | !Warning! 未经验证的值写入，如 "" 和 null 可能会造成不可预测的后果
                    config.setInterface(annotation.interfaceClass() == void.class ? holder.getType() : annotation.interfaceClass());
                    if (StringUtils.isNotBlank(annotation.interfaceName())) {
                        config.setInterface(annotation.interfaceName());
                    }
                    config.setVersion(annotation.version());
                    config.setGroup(annotation.group());
                    config.setUrl(annotation.url());
                    config.setClient(annotation.client());
                    config.setCheck(annotation.check());
                    config.setInit(annotation.init());
                    config.setLazy(annotation.lazy());
                    // annotation.stubEvent() 无对应值
                    config.setReconnect(annotation.reconnect());
                    config.setSticky(annotation.sticky());
                    config.setProxy(annotation.proxy());
                    config.setProtocol(annotation.protocol());
                    config.setTimeout(annotation.timeout());
                    config.setStub(annotation.stub());
                    config.setCluster(annotation.cluster());
                    config.setConnections(annotation.connections());
                    config.setCallbacks(annotation.callbacks());
                    config.setOnconnect(annotation.onconnect());
                    config.setOndisconnect(annotation.ondisconnect());
                    config.setOwner(annotation.owner());
                    config.setLayer(annotation.layer());
                    config.setRetries(annotation.retries());
                    config.setLoadbalance(annotation.loadbalance());
                    config.setAsync(annotation.async());
                    config.setActives(annotation.actives());
                    config.setSent(annotation.sent());
                    config.setMock(annotation.mock());
                    config.setValidation(annotation.validation());
                    config.setTimeout(annotation.timeout());
                    config.setCache(annotation.cache());
//                    config.setFilter(annotation.filter());
//                    config.setListener(annotation.listener());
//                    config.setParameters(annotation.parameters());
//                    config.setConsumer(annotation.consumer());
//                    config.setRegistry(annotation.registry());
                    config.setProtocol(annotation.protocol());
                    config.setTag(annotation.tag());
                    config.setMerger(annotation.merger());
//                    config.setMethods(annotation.methods());
                    config.setId(annotation.id());
//                    config.setProvidedBy(annotation.providedBy());
                    config.setScope(annotation.scope());
                    // 注册引用
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
