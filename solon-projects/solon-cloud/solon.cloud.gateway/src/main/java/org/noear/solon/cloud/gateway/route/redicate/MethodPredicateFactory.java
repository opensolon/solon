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
package org.noear.solon.cloud.gateway.route.redicate;

import org.noear.solon.cloud.gateway.exchange.ExPredicate;
import org.noear.solon.cloud.gateway.exchange.ExContext;
import org.noear.solon.cloud.gateway.route.RoutePredicateFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 路由方法匹配检测器
 * @author poppoppuppylove
 *
 */
public class MethodPredicateFactory implements RoutePredicateFactory {
    // 定义允许的HTTP方法
    private static final Set<String> VALID_METHODS = new HashSet<>(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"));

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

        public MethodPredicate(String config) {
            if (config == null || config.trim().isEmpty()) {
                throw new IllegalArgumentException("MethodPredicate configuration cannot be null or empty");
            }

            // 将配置的请求方法字符串拆分为数组，转换为大写，并过滤掉无效的HTTP方法
            methods = new HashSet<>();
            for (String method : config.split(",")) {
                String trimmedMethod = method.trim().toUpperCase();
                if (VALID_METHODS.contains(trimmedMethod)) {
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