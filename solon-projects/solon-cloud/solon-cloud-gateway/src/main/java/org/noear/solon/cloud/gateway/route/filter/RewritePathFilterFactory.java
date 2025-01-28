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

import java.util.regex.Pattern;

/**
 * 重写路径路由过滤器
 * 当配置信息为 RewritePath=/red/(?<segment>.*), /$\{segment}时，
 * 该处理器会将路径进行如下替换   /red/(?<segment>.*) => /$\{segment}
 * 例:
 * http://ip:port/red/hello => http://ip2:port2/hello
 *
 * @author TuZhe
 * @since 2.9
 **/
public class RewritePathFilterFactory implements RouteFilterFactory {

    @Override
    public String prefix() {
        return "RewritePath"; //魔法值, 不建议
    }

    @Override
    public ExFilter create(String config) {
        return new RewritePathFilter(config);
    }

    public static class RewritePathFilter implements ExFilter {
        private final String replacement;
        private final Pattern pattern;

        // if [RewritePath=/red/hello, /hello] then config = [/red/hello, /hello]]
        public RewritePathFilter(String config) {
            if (Utils.isBlank(config)) {
                throw new IllegalArgumentException("RewritePathFilter config cannot be blank");
            }

            String[] parts = config.split(","); //应该统一符号常量
            if (parts.length != 2) {
                throw new IllegalArgumentException("RewritePathFilter config is wrong: " + config);
            }
            String regex = parts[0].trim();
            String rawReplacement = parts[1].trim();

            if (!regex.startsWith("/") || !rawReplacement.startsWith("/")) {
                throw new IllegalArgumentException("RewritePathFilter config is wrong, path must be start with slash, config is : " + config);
            }
            pattern = Pattern.compile(regex);
            replacement = rawReplacement.replace("$\\", "$");
        }

        @Override
        public Baba<Void> doFilter(ExContext ctx, ExFilterChain chain) {
            String path = ctx.newRequest().getPath();

            String newPath = pattern.matcher(path).replaceAll(replacement);
            ctx.newRequest().path(newPath);
            return chain.doFilter(ctx);
        }
    }
}
