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
import org.noear.solon.core.route.PathRule;

/**
 * 路径检测器
 *
 * @author noear
 * @since 2.9
 */
public class PathPredicateFactory implements RoutePredicateFactory {
    @Override
    public String prefix() {
        return "Path";
    }

    @Override
    public ExPredicate create(String config) {
        return new PathPredicate(config);
    }

    public static class PathPredicate implements ExPredicate {
        private PathRule rule;

        public PathPredicate(String config) {
            rule = new PathRule();
            rule.include(config);
        }

        @Override
        public boolean test(ExContext ctx) {
            return rule.test(ctx.rawPath());
        }
    }
}
