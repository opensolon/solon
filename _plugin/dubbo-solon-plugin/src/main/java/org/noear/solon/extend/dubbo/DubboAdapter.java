package org.noear.solon.extend.dubbo;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;
import com.alibaba.dubbo.remoting.transport.ExceedPayloadLimitException;
import org.noear.solon.XApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XMap;

import java.io.IOException;

public class DubboAdapter {
    public void adapter() throws Exception {
        // 当前应用配置
        XMap props = XApp.cfg().getXmap("dubbo.application");
        if(props.size() == 0){
            props.put("name","dubbo-service-provider");
        }
        ApplicationConfig application = new ApplicationConfig();
        application.setParameters(props);
        //application.setName("hello-world-app-provider");

        // 连接注册中心配置
        props = XApp.cfg().getXmap("dubbo.registry");
        if(props.size() == 0){
            props.put("address","A/N");
        }
        RegistryConfig registry = new RegistryConfig();
        registry.setParameters(props);
        //registry.setAddress("zookeeper://192.168.133.129:2181");//这里使用zookeeper作为服务注册中心

        // 服务提供者协议配置
        ProtocolConfig protocol = new ProtocolConfig();
        protocol.setName("dubbo");
        protocol.setPort(XApp.global().port() + 20000);
        //protocol.setThreads(200);

        // 注意：ServiceConfig为重对象，内部封装了与注册中心的连接，以及开启服务端口

        // 服务提供者暴露服务配置
        Aop.beanOnloaded(()->{
            Aop.beanForeach((name,bw)->{
                if(bw.remoting()){

                    Class<?>[] ifs = bw.clz().getInterfaces();
                    if(ifs.length == 1) {
                        ServiceConfig service = new ServiceConfig(); // 此实例很重，封装了与注册中心的连接，请自行缓存，否则可能造成内存和连接泄漏
                        service.setApplication(application);
                        service.setRegistry(registry); // 多个注册中心可以用setRegistries()
                        service.setProtocol(protocol); // 多个协议可以用setProtocols()
                        service.setInterface(ifs[0]);
                        service.setRef(bw.raw());
                        service.setVersion("1.0.0");

                        // 暴露及注册服务
                        service.export();
                        try {
                            System.in.read(); //需要等待
                        }catch (IOException ex){
                            throw new RuntimeException(ex);
                        }
                    }
                }
            });
        });
    }
}
