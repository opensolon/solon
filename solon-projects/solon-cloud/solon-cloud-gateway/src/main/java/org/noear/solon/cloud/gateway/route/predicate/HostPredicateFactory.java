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
import org.noear.solon.cloud.gateway.exchange.ExContext;
import org.noear.solon.cloud.gateway.exchange.ExPredicate;
import org.noear.solon.cloud.gateway.route.RoutePredicateFactory;
import org.noear.solon.core.util.PathMatcher;

/**
 * 路由 Host 匹配检测器
 *
 * @author noear
 * @since 2.9
 */
public class HostPredicateFactory implements RoutePredicateFactory {
    @Override
    public String prefix() {
        return "Host";
    }

    @Override
    public ExPredicate create(String config) {
        return new HostPredicate(config);
    }

    public static class HostPredicate implements ExPredicate {
        private PathMatcher rule;

        /**
         * @param config (Host=*.demo.com)
         */
        public HostPredicate(String config) {
            if (Utils.isBlank(config)) {
                throw new IllegalArgumentException("HostPredicate config cannot be blank");
            }

            rule = PathMatcher.get(config, false); //借用路径规则
        }

        @Override
        public boolean test(ExContext ctx) {
            return rule.matches(ctx.rawURI().getHost());
        }
    }
}
