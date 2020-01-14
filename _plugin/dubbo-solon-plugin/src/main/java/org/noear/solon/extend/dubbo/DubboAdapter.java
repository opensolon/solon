package org.noear.solon.extend.dubbo;

import org.apache.dubbo.config.*;
import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.core.XMap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DubboAdapter {
    protected ApplicationConfig application;
    protected RegistryConfig registry;
    protected ProtocolConfig protocol;
    protected ConsumerConfig consumer;
    protected Map<Class<?>, ReferenceConfig> refMap = new ConcurrentHashMap<>();

    private static DubboAdapter _global;

    public static DubboAdapter global() {
        if (_global == null) {
            _global = new DubboAdapter();
        }

        return _global;
    }

    private DubboAdapter() {
        // 当前应用配置
        //
        XMap props = null;
        {
            application = new ApplicationConfig();
            props = XApp.cfg().getXmap("dubbo.application");
            if (props.containsKey("name") == false) {
                props.put("name", "dubbo-service-demo");
            }
            XUtil.bindTo(props, application);

            MonitorConfig monitor = new MonitorConfig();
            props = XApp.cfg().getXmap("dubbo.monitor");
            if (props.size() > 0) {
                monitor.setParameters(props);
                application.setMonitor(monitor);
            }
        }


        // 连接注册中心配置
        //
        {
            registry = new RegistryConfig();
            props = XApp.cfg().getXmap("dubbo.registry");
            if (props.containsKey("address") == false) {
                props.put("address", "A/N");
            }
            if (props.containsKey("group") == false) {
                props.put("group", "dubbo");
            }
            registry.setParameters(props);
        }


        // 服务提供者协议配置
        //
        {
            protocol = new ProtocolConfig();
            props = XApp.cfg().getXmap("dubbo.protocol");
            if (props.containsKey("name") == false) {
                props.put("name", "dubbo");
            }
            if (props.containsKey("port") == false) {
                props.put("port", "" + (XApp.global().port() + 20000));
            }
            protocol.setParameters(props);
        }

        {
            consumer = new ConsumerConfig();
            props = XApp.cfg().getXmap("dubbo.consumer");
            if (props.containsKey("check") == false) {
                props.put("check", "false");
            }
            if (props.containsKey("timeout") == false) {
                props.put("timeout", "3000");
            }
            consumer.setParameters(props);
        }
    }

    public void regService(ServiceConfig cfg) {
        cfg.setRegistry(registry); // 多个注册中心可以用setRegistries()
        cfg.setProtocol(protocol); // 多个协议可以用setProtocols()

        cfg.export();
    }

    public <T> T get(Class<T> clz) {
        return get(clz, null, "1.0.0");
    }

    public <T> T get(Class<T> clz, String url) {
        return get(clz, url, "1.0.0");
    }

    public <T> T get(Class<T> clz, String url, String ver) {
        ReferenceConfig<T> cfg = refMap.get(clz);
        if (cfg == null) {
            cfg = new ReferenceConfig<T>(); // 此实例很重，封装了与注册中心的连接以及与提供者的连接，请自行缓存，否则可能造成内存和连接泄漏
            cfg.setRegistry(registry); // 多个注册中心可以用setRegistries()
            cfg.setInterface(clz);
            cfg.setConsumer(consumer);
            cfg.setVersion(ver);
            cfg.setUrl(url);

            refMap.putIfAbsent(clz, cfg);
        }

        return cfg.get();
    }
}
