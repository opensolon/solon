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
 * 添加跳转过滤器
 *
 * @author noear
 * @since 2.9
 */
public class RedirectToFilterFactory implements RouteFilterFactory {
    @Override
    public String prefix() {
        return "RedirectTo";
    }

    @Override
    public ExFilter create(String config) {
        return new RedirectToFilter(config);
    }

    public static class RedirectToFilter implements ExFilter {
        private int code;
        private String url;
        private boolean addQuery = false;

        /**
         * @param config (RedirectTo=code,url,addQuery)
         */
        public RedirectToFilter(String config) {
            if (Utils.isBlank(config)) {
                throw new IllegalArgumentException("RedirectToFilter config cannot be blank");
            }

            String[] parts = config.split(",");

            if (parts.length < 2) {
                throw new IllegalArgumentException("RedirectToFilter config is wrong: " + config);
            }

            code = Integer.parseInt(parts[0]);
            url = parts[1];
            if (parts.length > 2) {
                addQuery = Boolean.parseBoolean(parts[2]);
            }
        }

        @Override
        public Baba<Void> doFilter(ExContext ctx, ExFilterChain chain) {
            if (addQuery) {
                ctx.newResponse().redirect(code, url + ctx.rawQueryString());
            } else {
                ctx.newResponse().redirect(code, url);
            }

            //直接结束，不再 chain.doFilter(ctx);
            return Baba.complete();
        }
    }
}
