package org.noear.solon.extend.hook.integration;

import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.extend.hook.HookBus;
import org.noear.solon.extend.hook.HookHandler;
import org.noear.solon.extend.hook.annotation.HookAction;

/**
 * @author noear
 * @since 1.8
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        context.beanBuilderAdd(HookAction.class, (clz, bw, anno) -> {
            if (bw.raw() instanceof HookHandler) {
                HookBus.subscribe(anno.value(), bw.raw());
            }
        });
    }
}
