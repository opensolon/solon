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
package org.noear.solon.cloud.gateway.route.predicate;

import org.noear.solon.cloud.gateway.exchange.ExPredicate;
import org.noear.solon.cloud.gateway.exchange.ExContext;
import org.noear.solon.cloud.gateway.route.RoutePredicateFactory;

import java.util.regex.Pattern;

/**
 * 路由 Cookie 匹配检测器
 * @author poppoppuppylove
 *
 */
public class CookiePredicateFactory implements RoutePredicateFactory {
    @Override
    public String prefix() {
        return "Cookie";
    }

    @Override
    public ExPredicate create(String config) {
        String[] parts = config.split(",", 2);
        // 如果 parts 长度不为 2，则表示配置格式有误。
        if (parts.length != 2) {
            throw new IllegalArgumentException("Cookie predicate requires both cookie name and regex.");
        }

        String cookieName = parts[0].trim();
        if (cookieName.isEmpty()) {
            throw new IllegalArgumentException("Cookie name cannot be empty.");
        }

        String regex = parts[1].trim();
        if (regex.isEmpty()) {
            throw new IllegalArgumentException("Regex cannot be empty.");
        }

        try {
            return new CookiePredicate(cookieName, regex);
        } catch (PatternSyntaxException e) {
            throw new IllegalArgumentException("Invalid regex pattern: " + regex, e);
        }
    }

    public static class CookiePredicate implements ExPredicate {
        private final String cookieName;
        private final Pattern pattern;

        public CookiePredicate(String cookieName, String regex) {
            this.cookieName = cookieName;
            this.pattern = Pattern.compile(regex);  // 编译正则表达式
        }

        @Override
        public boolean test(ExContext ctx) {
            String cookieValue = ctx.rawCookie(cookieName);
            // 如果 cookie 存在且值匹配正则表达式，则返回 true；否则返回 false。
            return cookieValue != null && pattern.matcher(cookieValue).matches();
        }
    }
}
