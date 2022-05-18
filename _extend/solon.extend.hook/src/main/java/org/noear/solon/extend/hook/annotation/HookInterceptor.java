package org.noear.solon.extend.hook.annotation;

import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.extend.hook.HookBus;

/**
 * @author noear
 * @since 1.8
 */
public class HookInterceptor implements Interceptor {
    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        HookBefore before = inv.method().getAnnotation(HookBefore.class);
        HookAfter after = inv.method().getAnnotation(HookAfter.class);

        if (before != null) {
            HookBus.publish(before.value(), inv.argsAsMap());
        }

        Object obj = inv.invoke();

        if (after != null) {
            HookBus.publish(after.value(), inv.argsAsMap());
        }

        return obj;
    }
}
