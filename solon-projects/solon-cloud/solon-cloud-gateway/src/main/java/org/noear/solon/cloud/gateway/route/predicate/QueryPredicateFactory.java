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

import java.util.regex.Pattern;

/**
 * 路由请求查询参数匹配
 *
 * @author noear
 * @since 2.9
 */
public class QueryPredicateFactory implements RoutePredicateFactory {
    @Override
    public String prefix() {
        return "Query";
    }


    @Override
    public ExPredicate create(String config) {
        return new QueryPredicate(config);
    }

    private static class QueryPredicate implements ExPredicate {
        private String queryName;
        private Pattern pattern;

        /**
         * @param config (Query=token)(Query=token, ^user.)
         * */
        public QueryPredicate(String config) {
            if (Utils.isBlank(config)) {
                throw new IllegalArgumentException("QueryPredicate config cannot be blank");
            }

            String[] parts = config.split(",");

            if (parts.length == 0) {
                throw new IllegalArgumentException("QueryPredicate config is wrong: " + config);
            }

            //queryName
            queryName = parts[0].trim();
            if (Utils.isEmpty(queryName)) {
                throw new IllegalArgumentException("Query name cannot be empty.");
            }

            //pattern
            if (parts.length > 1) {
                String regex = parts[1].trim();
                if (Utils.isEmpty(regex)) {
                    throw new IllegalArgumentException("Query regex cannot be empty.");
                } else {
                    pattern = Pattern.compile(regex);
                }
            }
        }

        @Override
        public boolean test(ExContext exContext) {
            String value = exContext.rawQueryParam(queryName);

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