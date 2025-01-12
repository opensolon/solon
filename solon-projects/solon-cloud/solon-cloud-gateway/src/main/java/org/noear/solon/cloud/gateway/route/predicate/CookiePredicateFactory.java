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

import java.util.regex.Pattern;

/**
 * 路由 Cookie 匹配检测器
 *
 * @author poppoppuppylove
 * @since 2.9
 */
public class CookiePredicateFactory implements RoutePredicateFactory {
    @Override
    public String prefix() {
        return "Cookie";
    }

    @Override
    public ExPredicate create(String config) {
        return new CookiePredicate(config);
    }

    public static class CookiePredicate implements ExPredicate {
        private final String cookieName;
        private Pattern pattern;

        /**
         * @param config (Cookie=token)(Cookie=token, ^user.)
         * */
        public CookiePredicate(String config) {
            if (Utils.isBlank(config)) {
                throw new IllegalArgumentException("CookiePredicate config cannot be blank");
            }

            String[] parts = config.split(",");

            if (parts.length == 0) {
                throw new IllegalArgumentException("CookiePredicate config format is wrong");
            }

            //cookieName
            cookieName = parts[0].trim();
            if (Utils.isEmpty(cookieName)) {
                throw new IllegalArgumentException("Cookie name cannot be empty.");
            }

            //pattern
            if (parts.length > 1) {
                String regex = parts[1].trim();
                if (Utils.isEmpty(regex)) {
                    throw new IllegalArgumentException("Cookie regex cannot be empty.");
                } else {
                    pattern = Pattern.compile(regex);  // 编译正则表达式
                }
            }
        }

        @Override
        public boolean test(ExContext ctx) {
            String value = ctx.rawCookie(cookieName);

            if (value == null) {
                //没找到
                return false;
            }

            if (pattern == null) {
                //不需要匹配（找到就行）
                return true;
            } else {
                //需要匹配检测
                return pattern.matcher(value).find();
            }
        }
    }
}