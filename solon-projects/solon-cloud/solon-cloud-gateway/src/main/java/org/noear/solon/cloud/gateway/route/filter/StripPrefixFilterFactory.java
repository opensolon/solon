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
import org.noear.solon.cloud.gateway.exchange.ExFilter;
import org.noear.solon.cloud.gateway.exchange.ExContext;
import org.noear.solon.cloud.gateway.exchange.ExFilterChain;
import org.noear.solon.cloud.gateway.route.RouteFilterFactory;
import org.noear.solon.rx.Baba;

import java.util.Arrays;
import java.util.List;

/**
 * 路径去除前缀过滤器
 *
 * @author noear
 * @since 2.9
 */
public class StripPrefixFilterFactory implements RouteFilterFactory {

    @Override
    public String prefix() {
        return "StripPrefix";
    }

    @Override
    public ExFilter create(String config) {
        return new StripPrefixFilter(config);
    }

    public static class StripPrefixFilter implements ExFilter {
        private int parts;

        public StripPrefixFilter(String config) {
            if (Utils.isBlank(config)) {
                throw new IllegalArgumentException("StripPrefixFilter config cannot be blank");
            }

            this.parts = Integer.parseInt(config);
        }

        @Override
        public Baba<Void> doFilter(ExContext ctx, ExFilterChain chain) {
            //目标路径重组
            List<String> pathFragments = Arrays.asList(ctx.newRequest().getPath().split("/", -1));
            String newPath = "/" + String.join("/", pathFragments.subList(parts + 1, pathFragments.size()));
            ctx.newRequest().path(newPath);

            return chain.doFilter(ctx);
        }
    }
}