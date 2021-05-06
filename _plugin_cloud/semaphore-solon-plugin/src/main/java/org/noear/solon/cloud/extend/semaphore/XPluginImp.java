package org.noear.solon.cloud.extend.semaphore;

import org.noear.solon.SolonApp;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.extend.semaphore.impl.CloudBreakerServiceImpl;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.3
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        CloudManager.register(CloudBreakerServiceImpl.getInstance());
    }
}
