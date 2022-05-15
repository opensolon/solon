package org.noear.solon.addin;

import org.noear.solon.Solon;
import org.noear.solon.core.Aop;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.route.Router;

/**
 * 外接小程序
 *
 * @author noear
 * @since 1.7
 */
public abstract class Addin implements Plugin {
    private AopContext context;

    /**
     * 上下文
     * */
    public AopContext context() {
        if (context == null) {
            context = Aop.context().copy();
        }

        return context;
    }

    /**
     * 路由器
     * */
    public Router router() {
        return Solon.global().router();
    }
}
