package org.noear.solon.extend.dubbo;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;
import com.alibaba.dubbo.config.annotation.Service;
import org.noear.solon.XApp;
import org.noear.solon.core.XMap;

public class DubboServer {
    protected ApplicationConfig application;
    protected RegistryConfig registry;
    protected ProtocolConfig protocol;

    public DubboServer(XApp app) {
        // 当前应用配置
        //
        XMap props = app.prop().getXmap("dubbo.application");
        if (props.containsKey("name") == false) {
            props.put("name", "dubbo-service-provider");
        }
        application = new ApplicationConfig();
        application.setParameters(props);
        //application.setName("hello-world-app-provider");


        // 连接注册中心配置
        //
        props = app.prop().getXmap("dubbo.registry");
        if (props.containsKey("address") == false) {
            props.put("address", "A/N");
        }
        registry = new RegistryConfig();
        registry.setParameters(props);
        //registry.setAddress("zookeeper://192.168.133.129:2181");//这里使用zookeeper作为服务注册中心


        // 服务提供者协议配置
        //
        props = app.prop().getXmap("dubbo.protocol");
        if (props.containsKey("name") == false) {
            props.put("name", "dubbo");
        }
        if (props.containsKey("port") == false) {
            props.put("port", "" + (XApp.global().port() + 20000));
        }
        protocol = new ProtocolConfig();
        protocol.setParameters(props);
    }

    public void regService(ServiceConfig cfg){
        cfg.setApplication(application);
        cfg.setRegistry(registry); // 多个注册中心可以用setRegistries()
        cfg.setProtocol(protocol); // 多个协议可以用setProtocols()
        cfg.export();
    }
}
