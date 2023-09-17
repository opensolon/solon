package io.seata.solon.impl;

import io.seata.core.model.GlobalLockConfig;
import io.seata.rm.GlobalLockExecutor;
import io.seata.solon.annotation.GlobalLock;
import org.noear.solon.core.aspect.Invocation;

/**
 * 全局锁执行器
 *
 * @author noear
 * @since 2.5
 */
public class GlobalLockExecutorImpl implements GlobalLockExecutor {
    Invocation inv;
    GlobalLock anno;

    public GlobalLockExecutorImpl(Invocation inv, GlobalLock anno) {
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
