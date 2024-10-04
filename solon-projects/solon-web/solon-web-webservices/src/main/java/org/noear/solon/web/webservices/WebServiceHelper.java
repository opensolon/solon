package org.noear.solon.web.webservices;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;


/**
 * WS 助手
 *
 * @author noear
 * @since 1.0
 */
public class WebServiceHelper {
    /**
     * 发布 Web 服务
     */
    public static void publishWebService(String wsAddress, Object wsImplementInstance) {
        publishWebService(wsAddress, wsImplementInstance, null);
    }

    /**
     * 发布 Web 服务
     */
    public static void publishWebService(String wsAddress, Object wsImplementInstance, Class<?> wsInterfaceClass) {
        if (wsInterfaceClass == null) {
            wsInterfaceClass = wsImplementInstance.getClass();
        }

        JaxWsServerFactoryBean factory = new JaxWsServerFactoryBean();
        factory.setAddress(wsAddress);               // 地址
        factory.setServiceClass(wsInterfaceClass);   // 接口
        factory.setServiceBean(wsImplementInstance); // 实现
        factory.create();
    }

    /**
     * 创建 Web 服务客户端
     */
    public static <T> T createWebClient(String wsAddress, Class<? extends T> interfaceClass) {
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setAddress(wsAddress);           // 地址
        factory.setServiceClass(interfaceClass); // 接口
        return factory.create(interfaceClass);
    }
}