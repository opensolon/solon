package org.noear.solon.cloud.gateway.route.redicate;

import org.noear.solon.cloud.gateway.route.RoutePredicate;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.route.PathRule;

/**
 * 路径断言
 *
 * @author noear
 * @since 2.9
 */
public class PathPredicate implements RoutePredicate {
    private PathRule rule;

    @Override
    public void init(String config) {
        rule = new PathRule();
        rule.include(config);
    }

    @Override
    public boolean test(Context context) {
        return rule.test(context.pathNew());
    }
}
