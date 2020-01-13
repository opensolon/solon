package org.noear.solon.extend.dubbo;

import com.alibaba.dubbo.config.*;
import org.noear.solon.XApp;
import org.noear.solon.XUtil;

import java.util.Properties;

public class DubboAdapter {
    protected ApplicationConfig application;
    protected RegistryConfig registry;
    protected ProtocolConfig protocol;

    private static DubboAdapter _global;
    public static DubboAdapter global(){
        if(_global == null){
            _global = new DubboAdapter();
        }

        return _global;
    }

    private DubboAdapter() {
        // 当前应用配置
        //
        Properties props = null;
        {
            props = XApp.cfg().getProp("dubbo.application");
            if (props.containsKey("name") == false) {
                props.put("name", "dubbo-service-demo");
            }

            application = new ApplicationConfig();
            XUtil.bindProps(props, application);
        }


        // 连接注册中心配置
        //
        {
            props = XApp.cfg().getProp("dubbo.registry");
            if (props.containsKey("address") == false) {
                props.put("address", "A/N");
            }
            if (props.containsKey("group") == false) {
                props.put("group", "dubbo");
            }

            registry = new RegistryConfig();
            XUtil.bindProps(props, registry);
        }


        // 服务提供者协议配置
        //
        {
            props = XApp.cfg().getProp("dubbo.protocol");
            if (props.containsKey("name") == false) {
                props.put("name", "dubbo");
            }
            if (props.containsKey("port") == false) {
                props.put("port", "" + (XApp.global().port() + 20000));
            }

            protocol = new ProtocolConfig();
            XUtil.bindProps(props, protocol);
        }
    }

    public void regService(ServiceConfig cfg) {
        cfg.setApplication(application);
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
        ReferenceConfig<T> cfg = new ReferenceConfig<T>(); // 此实例很重，封装了与注册中心的连接以及与提供者的连接，请自行缓存，否则可能造成内存和连接泄漏
        cfg.setApplication(application);
        cfg.setRegistry(registry); // 多个注册中心可以用setRegistries()
        cfg.setInterface(clz);
        cfg.setVersion(ver);
        cfg.setUrl(url);

        return cfg.get();
    }
}
