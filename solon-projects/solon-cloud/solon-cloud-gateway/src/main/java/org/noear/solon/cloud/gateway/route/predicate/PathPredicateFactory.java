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
import org.noear.solon.core.util.PathMatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * 路由 Path 匹配检测器
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
        private final List<PathMatcher> rules;
        private int depthMin;

        /**
         * @param config (Path=/demo/**) | (Path=/demo/**,/test/**)
         */
        public PathPredicate(String config) {
            if (Utils.isBlank(config)) {
                throw new IllegalArgumentException("PathPredicate config cannot be blank");
            }

            rules = new ArrayList<>();
            for (String path : config.split(",")) {
                String trimmedPath = path.trim();

                if (trimmedPath.length() > 0) {
                    PathMatcher rule = PathMatcher.get(trimmedPath);
                    rules.add(rule);

                    if (depthMin == 0 || depthMin > rule.depth()) {
                        depthMin = rule.depth(); //用最浅的作代表
                    }
                }
            }
        }

        /**
         * 获取路径常量深度
         */
        public int depth() {
            return depthMin;
        }

        @Override
        public boolean test(ExContext ctx) {
            for (PathMatcher rule : rules) {
                if (rule.matches(ctx.rawPath())) {
                    return true;
                }
            }

            return false;
        }
    }
}
