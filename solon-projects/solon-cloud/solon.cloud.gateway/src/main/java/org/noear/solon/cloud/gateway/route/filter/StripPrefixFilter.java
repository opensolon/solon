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
package org.noear.solon.cloud.gateway.route.filter;

import org.noear.solon.cloud.gateway.route.RouteFilter;
import org.noear.solon.cloud.gateway.rx.ExContext;
import org.noear.solon.cloud.gateway.rx.RxFilterChain;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

/**
 * 去除前缀过滤器
 *
 * @author noear
 * @since 2.9
 */
public class StripPrefixFilter implements RouteFilter {
    private int parts;

    @Override
    public RouteFilter init(String config) {
        this.parts = Integer.parseInt(config);
        return this;
    }

    @Override
    public Mono<Void> doFilter(ExContext ctx, RxFilterChain chain) {
        //目标路径重组
        List<String> pathFragments = Arrays.asList(ctx.newRequest().getPath().split("/", -1));
        String newPath = "/" + String.join("/", pathFragments.subList(parts + 1, pathFragments.size()));
        ctx.newRequest().path(newPath);

        return chain.doFilter(ctx);
    }
}
