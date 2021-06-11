package org.noear.solon.auth;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author noear
 * @since 1.4
 */
public class AuthRuleHandler implements Handler {
    private List<AuthRule> rules = new ArrayList<>();

    public List<AuthRule> rules() {
        return rules;
    }

    @Override
    public void handle(Context ctx) throws Throwable {
        for (AuthRule r : rules) {
            r.handle(ctx);
            if (ctx.getHandled()) {
                break;
            }
        }
    }
}
