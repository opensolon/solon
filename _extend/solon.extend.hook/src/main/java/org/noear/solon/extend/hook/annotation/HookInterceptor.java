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
        Hook hook = inv.method().getAnnotation(Hook.class);

        if (hook != null) {
            HookBus.onBefore(hook.value(), inv.argsAsMap());
        }

        Object obj = inv.invoke();

        if (hook != null) {
            HookBus.onAfter(hook.value(), inv.argsAsMap());
        }

        return obj;
    }
}
