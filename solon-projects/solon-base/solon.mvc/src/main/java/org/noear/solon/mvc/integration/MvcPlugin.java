package org.noear.solon.mvc.integration;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.FactoryManager;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.AppBeanLoadEndEvent;
import org.noear.solon.core.handle.ContextPathFilter;
import org.noear.solon.mvc.MvcFactoryImpl;

/**
 * @author noear
 * @since 2.7
 */
public class MvcPlugin implements Plugin {
    @Override
    public void start(AppContext context) throws Throwable {
        context.onEvent(AppBeanLoadEndEvent.class, e -> {
            //3.1.尝试设置 context-path
            if (Utils.isNotEmpty(Solon.cfg().serverContextPath())) {
                Solon.app().filterIfAbsent(-99, new ContextPathFilter());
            }
        });

        FactoryManager.mvcFactory(new MvcFactoryImpl());
    }
}
