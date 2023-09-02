package org.noear.solon.vault.integration;

import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.vault.annotation.VaultInject;

/**
 * @author noear
 * @since 1.9
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AppContext context) {
        context.beanInjectorAdd(VaultInject.class, new VaultBeanInjector());
    }
}
