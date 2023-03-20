package org.noear.solon.cloud.impl;

import org.noear.solon.cloud.CloudJobHandler;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.handle.Context;

/**
 * CloubJob 类运行器（支持非单例）
 *
 * @author noear
 * @since 2.2
 */
public class CloudJobBean implements CloudJobHandler {
    BeanWrap target;

    public CloudJobBean(BeanWrap target) {
        this.target = target;
    }

    @Override
    public void handle(Context ctx) throws Throwable {
        ((CloudJobHandler) target.get()).handle(ctx);
    }
}
