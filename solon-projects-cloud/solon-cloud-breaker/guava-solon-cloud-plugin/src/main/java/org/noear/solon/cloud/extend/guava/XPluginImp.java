package org.noear.solon.cloud.extend.guava;

import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.extend.guava.impl.CloudBreakerServiceImpl;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.3
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AppContext context) {
        CloudManager.register(new CloudBreakerServiceImpl());
    }
}
