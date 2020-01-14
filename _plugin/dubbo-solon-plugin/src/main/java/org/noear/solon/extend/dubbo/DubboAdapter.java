package org.noear.solon.extend.dubbo;

import org.apache.dubbo.config.*;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.rpc.model.ApplicationModel;
import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.core.XMap;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class DubboAdapter {
    protected ApplicationConfig application;
    protected Map<String, ReferenceConfig> refMap = new ConcurrentHashMap<>();

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

            ApplicationModel.getConfigManager().addConfig(application);
        }


        // 连接注册中心配置
        //
        {
            RegistryConfig registry = new RegistryConfig();
            props = XApp.cfg().getXmap("dubbo.registry");
            if (props.containsKey("address") == false) {
                props.put("address", "A/N");
            }
            registry.setParameters(props);
            ApplicationModel.getConfigManager().addRegistry(registry);
        }


        // 服务提供者协议配置
        //
        {
            ProtocolConfig protocol = new ProtocolConfig();
            props = XApp.cfg().getXmap("dubbo.protocol");
            if (props.containsKey("name") == false) {
                props.put("name", "dubbo");
            }
            if (props.containsKey("port") == false) {
                props.put("port", "" + (XApp.global().port() + 20000));
            }
            protocol.setParameters(props);
            ApplicationModel.getConfigManager().addProtocol(protocol);
        }

        {
            ConsumerConfig  consumer = new ConsumerConfig();
            props = XApp.cfg().getXmap("dubbo.consumer");
            if (props.containsKey("check") == false) {
                props.put("check", "false");
            }
            if (props.containsKey("timeout") == false) {
                props.put("timeout", "3000");
            }
            consumer.setParameters(props);
            ApplicationModel.getConfigManager().addConsumer(consumer);
        }
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
            blockThread.stop();
            blockThread = null;
        }
    }

    public void regService(ServiceConfig cfg) {
        Properties prop = XApp.cfg().getProp("dubbo.service." + cfg.getInterface());
        if (prop.size() > 0) {
            XUtil.bindTo(prop, cfg);
        }

        cfg.export();
        startBlock();
    }

    public <T> T getService(Class<T> clz, Reference ref) {
        //生成带版本号的key
        String clzKey = null;
        if (ref == null) {
            clzKey = clz.getName();
        } else {
            clzKey = clz.getName() + "_" + ref.version();
        }


        ReferenceConfig<T> cfg = refMap.get(clzKey);
        if (cfg == null) {
            cfg = new ReferenceConfig<T>(ref); // 此实例很重，封装了与注册中心的连接以及与提供者的连接，请自行缓存，否则可能造成内存和连接泄漏
            cfg.setInterface(clz);

            Properties prop = XApp.cfg().getProp("dubbo.reference." + cfg.getInterface());
            if (prop.size() > 0) {
                XUtil.bindTo(prop, cfg);
            }

            refMap.putIfAbsent(clzKey, cfg);
        }

        return cfg.get();
    }
}
