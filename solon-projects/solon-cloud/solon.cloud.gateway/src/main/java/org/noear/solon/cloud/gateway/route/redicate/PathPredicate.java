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

import org.noear.solon.cloud.gateway.route.RoutePredicate;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.route.PathRule;

/**
 * 路径检测器
 *
 * @author noear
 * @since 2.9
 */
public class PathPredicate implements RoutePredicate {
    private PathRule rule;

    @Override
    public RoutePredicate init(String config) {
        rule = new PathRule();
        rule.include(config);
        return this;
    }

    @Override
    public boolean test(Context context) {
        return rule.test(context.pathNew());
    }
}
