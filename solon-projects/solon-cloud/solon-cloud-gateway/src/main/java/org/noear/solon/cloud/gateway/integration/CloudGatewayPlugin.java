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
package org.noear.solon.cloud.gateway.integration;

import org.noear.solon.Utils;
import org.noear.solon.boot.vertx.http.VxHandlerSupplier;
import org.noear.solon.boot.vertx.http.VxHandlerSupplierDefault;
import org.noear.solon.cloud.gateway.*;
import org.noear.solon.cloud.gateway.properties.GatewayProperties;
import org.noear.solon.cloud.gateway.route.*;
import org.noear.solon.core.*;

/**
 * @author noear
 * @since 2.9
 */
public class CloudGatewayPlugin implements Plugin {
    private static final String SOLON_CLOUD_GATEWAY = "solon.cloud.gateway";

    @Override
    public void start(AppContext context) throws Throwable {
        final Props gatewayProps = context.cfg().getProp(SOLON_CLOUD_GATEWAY);
        final GatewayProperties gatewayProperties;
        if (gatewayProps.size() > 0) {
            gatewayProperties = gatewayProps.toBean(GatewayProperties.class);
        } else {
            gatewayProperties = new GatewayProperties();
        }

        VxHandlerSupplierDefault webHandlerSupplier = new VxHandlerSupplierDefault();
        CloudGatewayHandler cloudGateway = new CloudGatewayHandler(webHandlerSupplier.get());

        //替代 solon.boot.vertx 的默认处理
        CloudGatewayHandlerSupplier gatewayHandlerSupplier = new CloudGatewayHandlerSupplier(cloudGateway);
        context.wrapAndPut(VxHandlerSupplier.class, gatewayHandlerSupplier);

        //添加默认过滤器
        if (Utils.isNotEmpty(gatewayProperties.getDefaultFilters())) {
            for (String defaultFilter : gatewayProperties.getDefaultFilters()) {
                cloudGateway.getConfiguration().routeDefaultFilter(RouteFactoryManager.buildFilter(defaultFilter));
            }
        }

        //注册 CloudRouteConfiguration
        context.wrapAndPut(CloudRouteRegister.class, cloudGateway.getConfiguration());

        //添加过注解滤器（可多个）
        context.subWrapsOfType(CloudGatewayFilter.class, bw -> {
            cloudGateway.getConfiguration().filter(bw.raw(), bw.index());
        });

        //添加路由处理器（可多个）
        context.subBeansOfType(RouteHandler.class, b -> {
            RouteFactoryManager.addHandler(b);
        });

        //添加路由过滤器工厂（可多个）
        context.subBeansOfType(RouteFilterFactory.class, b -> {
            RouteFactoryManager.addFactory(b);
        });

        //添加路由检测器工厂（可多个）
        context.subBeansOfType(RoutePredicateFactory.class, b -> {
            RouteFactoryManager.addFactory(b);
        });

        //加载配置（同步服务发现）
        CloudGatewayLocator gatewayLocator = new CloudGatewayLocator(gatewayProperties, cloudGateway.getConfiguration());
        context.lifecycle(-1, gatewayLocator);
    }
}