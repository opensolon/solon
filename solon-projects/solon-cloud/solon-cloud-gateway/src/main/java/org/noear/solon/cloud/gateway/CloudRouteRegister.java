/*
 * Copyright 2017-2024 noear.org and authors
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
package org.noear.solon.cloud.gateway;

import org.noear.solon.cloud.gateway.route.RouteSpec;

import java.util.function.Consumer;

/**
 * 分布式路由注册表
 *
 * @author noear
 * @since 2.9
 */
public interface CloudRouteRegister {
    /**
     * 路由登记
     *
     * @param id      标识
     * @param builder 路由构建器
     */
    CloudRouteRegister route(String id, Consumer<RouteSpec> builder);

    /**
     * 路由登记
     *
     * @param route 路由
     */
    CloudRouteRegister route(RouteSpec route);

    /**
     * 路由移除
     *
     * @param id 标识
     */
    CloudRouteRegister routeRemove(String id);
}