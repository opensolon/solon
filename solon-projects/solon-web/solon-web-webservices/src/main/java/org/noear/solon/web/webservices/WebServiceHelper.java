/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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