package io.seata.solon.impl;

import io.seata.core.model.GlobalLockConfig;
import io.seata.rm.GlobalLockExecutor;
import io.seata.solon.annotation.SeataLock;
import org.noear.solon.core.aspect.Invocation;

/**
 * @author noear
 * @since 2.4
 */
public class GlobalLockExecutorImpl implements GlobalLockExecutor {
    Invocation inv;
    SeataLock anno;

    public GlobalLockExecutorImpl(Invocation inv, SeataLock anno) {
        this.inv = inv;
        this.anno = anno;
    }

    @Override
    public Object execute() throws Throwable {
        return inv.invoke();
    }

    @Override
    public GlobalLockConfig getGlobalLockConfig() {
        GlobalLockConfig config = new GlobalLockConfig();
        config.setLockRetryInterval(anno.lockRetryInterval());
        config.setLockRetryTimes(anno.lockRetryTimes());
        return config;
    }
}
