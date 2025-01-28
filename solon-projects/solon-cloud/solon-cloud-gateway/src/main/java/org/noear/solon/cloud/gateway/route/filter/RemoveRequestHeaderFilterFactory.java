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
import org.noear.solon.rx.Baba;

/**
 * 移除请求头过滤器
 *
 * @author noear
 * @since 2.9
 */
public class RemoveRequestHeaderFilterFactory implements RouteFilterFactory {
    @Override
    public String prefix() {
        return "RemoveRequestHeader";
    }

    @Override
    public ExFilter create(String config) {
        return new RemoveRequestHeaderFilter(config);
    }

    public static class RemoveRequestHeaderFilter implements ExFilter {
        private final String[] names;

        /**
         * @param config (RemoveRequestHeader=name name2 name3)
         */
        public RemoveRequestHeaderFilter(String config) {
            if (Utils.isBlank(config)) {
                throw new IllegalArgumentException("RemoveRequestHeaderFilter config cannot be blank");
            }

            names = config.split(",");

            if (names.length == 0) {
                throw new IllegalArgumentException("RemoveRequestHeaderFilter config is wrong: " + config);
            }
        }

        @Override
        public Baba<Void> doFilter(ExContext ctx, ExFilterChain chain) {
            ctx.newRequest().headerRemove(names);
            return chain.doFilter(ctx);
        }
    }
}
