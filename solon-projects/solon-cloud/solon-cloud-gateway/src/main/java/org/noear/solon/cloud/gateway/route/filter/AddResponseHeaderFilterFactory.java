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
package org.noear.solon.cloud.gateway.route.filter;

import org.noear.solon.Utils;
import org.noear.solon.cloud.gateway.exchange.ExContext;
import org.noear.solon.cloud.gateway.exchange.ExFilter;
import org.noear.solon.cloud.gateway.exchange.ExFilterChain;
import org.noear.solon.cloud.gateway.route.RouteFilterFactory;
import org.noear.solon.rx.Completable;

/**
 * 添加响应头过滤器
 *
 * @author noear
 * @since 2.9
 */
public class AddResponseHeaderFilterFactory implements RouteFilterFactory {
    @Override
    public String prefix() {
        return "AddResponseHeader";
    }

    @Override
    public ExFilter create(String config) {
        return new AddResponseHeaderFilter(config);
    }

    public static class AddResponseHeaderFilter implements ExFilter {
        private final String name;
        private final String value;

        /**
         * @param config (AddResponseHeader=name,value)
         */
        public AddResponseHeaderFilter(String config) {
            if (Utils.isBlank(config)) {
                throw new IllegalArgumentException("AddResponseHeaderFilter config cannot be blank");
            }

            String[] parts = config.split(",");

            if (parts.length != 2) {
                throw new IllegalArgumentException("AddResponseHeaderFilter config is wrong: " + config);
            }

            this.name = parts[0];
            this.value = parts[1];

            if (Utils.isEmpty(name) || Utils.isEmpty(value)) {
                throw new IllegalArgumentException("AddResponseHeaderFilter config is wrong: " + config);
            }
        }

        @Override
        public Completable doFilter(ExContext ctx, ExFilterChain chain) {
            return Completable.create(emitter -> {
                chain.doFilter(ctx)
                        .doOnComplete(() -> {
                            ctx.newResponse().headerAdd(name, value);
                            emitter.onComplete();
                        })
                        .doOnError(err -> {
                            ctx.newResponse().headerAdd(name, value);
                            emitter.onError(err);
                        })
                        .subscribe();
            });
        }
    }
}
