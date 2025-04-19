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
package org.noear.solon.cloud.gateway;

import org.noear.solon.cloud.gateway.exchange.ExContext;
import org.noear.solon.cloud.gateway.exchange.ExFilterChain;
import org.noear.solon.core.util.RunUtil;
import org.noear.solon.rx.Completable;
import org.noear.solon.rx.CompletableEmitter;

/**
 * 分布式网关过滤器异步形态（对接同步 io）
 *
 * @author noear
 * @since 3.1
 */
public abstract class CloudGatewayFilterAsync implements CloudGatewayFilter {
    @Override
    public Completable doFilter(ExContext ctx, ExFilterChain chain) {
        return Completable.create(emitter -> {
            ctx.pause();
            RunUtil.async(() -> {
                doFilterAsync(ctx, chain, emitter);
            });
        });
    }

    protected abstract void doFilterAsync(ExContext ctx, ExFilterChain chain, CompletableEmitter emitter);
}
