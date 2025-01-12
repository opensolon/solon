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
package org.noear.solon.cloud.gateway.route.predicate;

import org.noear.solon.Utils;
import org.noear.solon.cloud.gateway.exchange.ExPredicate;
import org.noear.solon.cloud.gateway.exchange.ExContext;
import org.noear.solon.cloud.gateway.route.RoutePredicateFactory;
import org.noear.solon.core.handle.MethodType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 路由 Method 匹配检测器
 *
 * @author poppoppuppylove
 * @since 2.9
 */
public class MethodPredicateFactory implements RoutePredicateFactory {
    // 定义允许的HTTP方法
    private static final Set<String> VALID_METHODS = new HashSet<>(Arrays.asList(
            MethodType.GET.name(),
            MethodType.POST.name(),
            MethodType.PUT.name(),
            MethodType.DELETE.name(),
            MethodType.PATCH.name(),
            MethodType.OPTIONS.name(),
            MethodType.HEAD.name()));

    @Override
    public String prefix() {
        return "Method";
    }

    @Override
    public ExPredicate create(String config) {
        return new MethodPredicate(config);
    }

    public static class MethodPredicate implements ExPredicate {
        private final Set<String> methods;

        /**
         * @param config (Method=GET,POST)
         * */
        public MethodPredicate(String config) {
            if (Utils.isBlank(config)) {
                throw new IllegalArgumentException("MethodPredicate config cannot be blank");
            }

            methods = new HashSet<>();
            for (String method : config.split(",")) {
                //转换为大写
                String trimmedMethod = method.trim().toUpperCase();

                if (VALID_METHODS.contains(trimmedMethod)) {
                    //过滤掉无效的HTTP方法
                    methods.add(trimmedMethod);
                } else {
                    throw new IllegalArgumentException("Invalid HTTP method: " + trimmedMethod);
                }
            }

            if (methods.isEmpty()) {
                throw new IllegalArgumentException("No valid HTTP methods specified");
            }
        }

        @Override
        public boolean test(ExContext ctx) {
            // 测试当前请求的方法是否在配置的Set中
            return methods.contains(ctx.rawMethod());
        }
    }
}