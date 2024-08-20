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

import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

/**
 * 路由时间匹配检测器（After、Before）
 * @author poppoppuppylove
 *
 */
public class TimePredicateFactory implements RoutePredicateFactory {
    @Override
    public String prefix() {
        return "Time";
    }

    @Override
    public ExPredicate create(String config) {
        String[] parts = config.split("=", 2);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Time predicate requires both condition and datetime.");
        }

        String condition = parts[0].trim();
        if (condition.isEmpty()) {
            throw new IllegalArgumentException("condition cannot be empty.");
        }

        String dateTimeStr = parts[1].trim();
        if (dateTimeStr.isEmpty()) {
            throw new IllegalArgumentException("Datetime cannot be empty.");
        }

        try {
            ZonedDateTime dateTime = ZonedDateTime.parse(dateTimeStr);

            switch (condition) {
                case "After":
                    return new AfterPredicate(dateTime);
                case "Before":
                    return new BeforePredicate(dateTime);
                default:
                    throw new IllegalArgumentException("Unsupported time condition: " + condition);
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid datetime format: " + dateTimeStr, e);
        }
    }

    public static class AfterPredicate implements ExPredicate {
        private final ZonedDateTime dateTime;

        public AfterPredicate(ZonedDateTime dateTime) {
            this.dateTime = dateTime;
        }

        @Override
        public boolean test(ExContext ctx) {
            return ZonedDateTime.now().isAfter(dateTime);
        }
    }

    public static class BeforePredicate implements ExPredicate {
        private final ZonedDateTime dateTime;

        public BeforePredicate(ZonedDateTime dateTime) {
            this.dateTime = dateTime;
        }

        @Override
        public boolean test(ExContext ctx) {
            return ZonedDateTime.now().isBefore(dateTime);
        }
    }
}
