package org.noear.solon.cloud.gateway.redicate;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.route.PathRule;

import java.util.function.Predicate;

/**
 * 路径断言
 *
 * @author noear
 * @since 2.9
 */
public class PathPredicate implements Predicate<Context> {
    private final PathRule rule;

    public PathPredicate(String expr) {
        rule = new PathRule();
        rule.include(expr);
    }

    @Override
    public boolean test(Context context) {
        return rule.test(context.pathNew());
    }
}
