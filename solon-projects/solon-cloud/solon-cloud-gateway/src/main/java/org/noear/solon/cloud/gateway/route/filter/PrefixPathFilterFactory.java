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
 * 添加前缀路径过滤器
 *
 * @author noear
 * @since 2.9
 */
public class PrefixPathFilterFactory implements RouteFilterFactory {

    @Override
    public String prefix() {
        return "PrefixPath";
    }

    @Override
    public ExFilter create(String config) {
        return new PrefixPathFilter(config);
    }

    public static class PrefixPathFilter implements ExFilter {
        private String part;

        public PrefixPathFilter(String config) {
            if (Utils.isBlank(config)) {
                throw new IllegalArgumentException("PrefixPathFilter config cannot be blank");
            }

            if (config.startsWith("/")) {
                this.part = config;
            } else {
                this.part = "/" + config;
            }
        }

        @Override
        public Completable doFilter(ExContext ctx, ExFilterChain chain) {
            //目标路径重组
            String newPath = part + ctx.newRequest().getPath();
            ctx.newRequest().path(newPath);

            return chain.doFilter(ctx);
        }
    }
}