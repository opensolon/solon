package org.noear.solon.extend.dubbo3;

import org.apache.dubbo.config.*;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.rpc.model.ApplicationModel;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.core.NvMap;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class DubboAdapter {
    protected ApplicationConfig application;
    protected Map<String, ReferenceConfig> refMap = new ConcurrentHashMap<>();

    private static DubboAdapter _global;

    public static DubboAdapter global(SolonApp app) {
        if (_global == null) {
            _global = new DubboAdapter(app);
        }

        return _global;
    }


    private DubboAdapter(SolonApp app) {

        // 当前应用配置
        //
        NvMap props = null;
        {
            application = new ApplicationConfig();
            props = Solon.cfg().getXmap("dubbo.application");
            if (props.containsKey("name") == false) {
                props.put("name", "dubbo-service-demo");
            }

            Utils.bindTo(props, application);

            MonitorConfig monitor = new MonitorConfig();
            props = Solon.cfg().getXmap("dubbo.monitor");
            if (props.size() > 0) {
                monitor.setParameters(props);
                application.setMonitor(monitor);
            }

            ApplicationModel.defaultModel().getApplicationConfigManager()
                    .addConfig(application);
        }


        // 连接注册中心配置
        //
        {
            RegistryConfig registry = new RegistryConfig();
            props = Solon.cfg().getXmap("dubbo.registry");
            if (props.containsKey("address") == false) {
                props.put("address", "A/N");
            }
            registry.setParameters(props);
            ApplicationModel.defaultModel().getApplicationConfigManager()
                    .addRegistry(registry);
        }


        // 服务提供者协议配置
        //
        {
            ProtocolConfig protocol = new ProtocolConfig();
            props = Solon.cfg().getXmap("dubbo.protocol");
            protocol.setParameters(props);

            if (props.containsKey("name") == false) {
                props.put("name", "dubbo");
                protocol.setName("dubbo");
            }

            if (props.containsKey("port") == false) {
                int port = Solon.global().port() + 20000;
                props.put("port", String.valueOf(port));
                protocol.setPort(port);
            }

            ApplicationModel.defaultModel().getApplicationConfigManager()
                    .addProtocol(protocol);
        }

        // 服务消费者配置
        //
        {
            ConsumerConfig consumer = new ConsumerConfig();
            props = Solon.cfg().getXmap("dubbo.consumer");
            if (props.containsKey("check") == false) {
                props.put("check", "false");
            }
            if (props.containsKey("timeout") == false) {
                props.put("timeout", "3000");
            }

            consumer.setParameters(props);
            ApplicationModel.defaultModel().getApplicationConfigManager()
                    .addConfig(consumer);
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
            blockThread.interrupt();
            blockThread = null;
        }
    }

    public void regService(ServiceConfig cfg) {
        Properties prop = Solon.cfg().getProp("dubbo.service." + cfg.getInterface());
        if (prop.size() > 0) {
            Utils.bindTo(prop, cfg);
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

            Properties prop = Solon.cfg().getProp("dubbo.reference." + cfg.getInterface());
            if (prop.size() > 0) {
                Utils.bindTo(prop, cfg);
            }

            ReferenceConfig<T> l = refMap.putIfAbsent(clzKey, cfg);
            if(l != null){
                cfg = l;
            }
        }

        return cfg.get();
    }
}
