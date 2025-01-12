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
package org.noear.solon.web.webservices.integration;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.web.webservices.WebServiceHelper;

import javax.jws.WebService;
import javax.servlet.ServletConfig;
public class WebServiceServlet extends CXFNonSpringServlet {
    @Override
    protected void loadBus(ServletConfig sc) {
        // 设置 Bus
        setBus(sc);
        // 发布 Web 服务
        publishWebService();
    }

    private void setBus(ServletConfig sc) {
        super.loadBus(sc);
        Bus bus = getBus();
        BusFactory.setDefaultBus(bus);
    }

    private void publishWebService() {
        // 遍历所有标注了 @WebService 注解的接口
        WebServiceBeanBuilder wsBeanBuilder = Solon.context().getBean(WebServiceBeanBuilder.class);

        for (BeanWrap bw : wsBeanBuilder.getWsBeanWarps()) {
            // 获取 name 属性
            WebService anno = bw.clz().getAnnotation(WebService.class);
            String name = Utils.annoAlias(anno.name(), anno.serviceName());
            // 获取 Web 服务地址
            String wsAddress = getAddress(name, bw.clz());
            // 获取 Web 服务实现类（找到唯一的实现类）
            Class<?> wsImplementClass = bw.clz();
            // 获取实现类的实例
            Object wsImplementInstance = bw.raw();
            // 发布 Web 服务
            WebServiceHelper.publishWebService(wsAddress, wsImplementInstance, wsImplementClass);

        }
    }

    private String getAddress(String value, Class<?> wsInterfaceClass) {
        String address;
        if (Utils.isNotEmpty(value)) {
            // 若不为空，则为 value
            address = value;
        } else {
            // 若为空，则为类名
            address = wsInterfaceClass.getSimpleName();
        }

        // 确保最前面只有一个 /
        if (!address.startsWith("/")) {
            address = "/" + address;
        }

        address = address.replaceAll("\\/+", "/");
        return address;
    }
}