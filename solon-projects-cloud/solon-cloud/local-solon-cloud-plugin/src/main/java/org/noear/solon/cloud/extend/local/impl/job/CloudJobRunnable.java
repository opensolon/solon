package org.noear.solon.cloud.extend.local.impl.job;

import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudJobHandler;
import org.noear.solon.cloud.exception.CloudJobException;
import org.noear.solon.core.handle.ContextEmpty;

/**
 * 方法运行器
 *
 * @author noear
 * @since 1.6
 */
public class CloudJobRunnable implements Runnable {
    private CloudJobHandler handler;

    public CloudJobRunnable(CloudJobHandler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
            handler.handle(new ContextEmpty());
        } catch (Throwable e) {
            e = Utils.throwableUnwrap(e);
            throw new CloudJobException(e);
        }
    }
}
