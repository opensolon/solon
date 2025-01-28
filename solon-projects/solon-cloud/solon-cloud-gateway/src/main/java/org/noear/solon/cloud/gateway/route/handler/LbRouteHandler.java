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
package org.noear.solon.cloud.gateway.route.handler;

import org.noear.solon.cloud.gateway.exchange.ExContext;
import org.noear.solon.cloud.gateway.route.RouteFactoryManager;
import org.noear.solon.cloud.gateway.route.RouteHandler;
import org.noear.solon.core.LoadBalance;
import org.noear.solon.core.exception.StatusException;
import org.noear.solon.rx.Baba;

import java.net.URI;

/**
 * Lb 路由处理器
 *
 * @author noear
 * @since 2.9
 */
public class LbRouteHandler implements RouteHandler {
    @Override
    public String[] schemas() {
        return new String[]{"lb"};
    }

    @Override
    public Baba<Void> handle(ExContext ctx) {
        //构建新的目标
        URI targetUri = ctx.targetNew();

        String tmp = LoadBalance.get(targetUri.getHost()).getServer(targetUri.getPort());
        if (tmp == null) {
            throw new StatusException("The target service does not exist", 404);
        }

        //配置新目标
        targetUri = URI.create(tmp);
        ctx.targetNew(targetUri);

        //重新查找处理器
        RouteHandler handler = RouteFactoryManager.getHandler(targetUri.getScheme());

        if (handler == null) {
            throw new StatusException("The target handler does not exist", 404);
        }

        return handler.handle(ctx);
    }
}
