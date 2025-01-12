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

import java.time.ZonedDateTime;

/**
 * 路由时间 After 匹配检测器
 * 
 * @author poppoppuppylove
 * @since 2.9
 */
public class AfterPredicateFactory implements RoutePredicateFactory {
    @Override
    public String prefix() {
        return "After";
    }

    @Override
    public ExPredicate create(String config) {
        return new AfterPredicate(config);
    }

    public static class AfterPredicate implements ExPredicate {
        private final ZonedDateTime dateTime;

        /**
         * @param config (After=2017-01-20T17:42:47.789-07:00[America/Denver])
         */
        public AfterPredicate(String config) {
            if (Utils.isBlank(config)) {
                throw new IllegalArgumentException("AfterPredicate config cannot be blank");
            }

            this.dateTime = ZonedDateTime.parse(config);
        }

        @Override
        public boolean test(ExContext ctx) {
            return ZonedDateTime.now().isAfter(dateTime);
        }
    }
}
