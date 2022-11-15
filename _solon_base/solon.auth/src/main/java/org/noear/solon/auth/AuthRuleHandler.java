package org.noear.solon.auth;

import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;

import java.util.ArrayList;
import java.util.List;

/**
 * 验证规则处理者
 *
 * @author noear
 * @since 1.4
 */
public class AuthRuleHandler implements Handler {
    private String pathPrefix;

    /**
     * 设置规则路径前缀（用于支持 AuthAdapterSupplier 的前缀特性）
     * */
    public void setPathPrefix(String pathPrefix) {
        this.pathPrefix = pathPrefix;
    }

    private List<AuthRule> rules = new ArrayList<>();

    public void addRule(AuthRule rule) {
        rules.add(rule);
    }


    @Override
    public void handle(Context ctx) throws Throwable {
        //尝试前缀过滤
        if (Utils.isNotEmpty(pathPrefix)) {
            if (ctx.pathNew().startsWith(pathPrefix) == false) {
                return;
            }
        }

        //尝试规则处理
        for (AuthRule r : rules) {
            if (ctx.getHandled()) {
                return;
            }

            r.handle(ctx);
        }
    }
}
