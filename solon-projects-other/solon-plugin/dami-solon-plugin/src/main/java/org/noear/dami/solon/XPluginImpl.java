package org.noear.dami.solon;

import org.noear.dami.api.Coder;
import org.noear.dami.bus.Interceptor;
import org.noear.dami.solon.annotation.Dami;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.DamiTopicMappingBuilder;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 2.5
 */
public class XPluginImpl implements Plugin {
    @Override
    public void start(AppContext context) throws Throwable {
        context.beanBuilderAdd(Dami.class, new DamiTopicMappingBuilder());

        context.subWrapsOfType(Interceptor.class, wrap -> {
            org.noear.dami.Dami.intercept(wrap.index(), wrap.raw());
        });

        context.getBeanAsync(Coder.class, bean -> {
            org.noear.dami.Dami.api().setCoder(bean);
        });
    }
}
