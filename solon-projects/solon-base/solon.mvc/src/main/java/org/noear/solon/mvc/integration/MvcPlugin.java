package org.noear.solon.mvc.integration;

import org.noear.solon.core.AppContext;
import org.noear.solon.core.FactoryManager;
import org.noear.solon.core.Plugin;
import org.noear.solon.mvc.MvcFactoryDefault;

/**
 * @author noear
 * @since 2.7
 */
public class MvcPlugin implements Plugin {
    @Override
    public void start(AppContext context) throws Throwable {
        FactoryManager.mvcFactory(new MvcFactoryDefault());
    }
}
