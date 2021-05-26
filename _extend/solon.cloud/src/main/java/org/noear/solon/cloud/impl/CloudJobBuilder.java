package org.noear.solon.cloud.impl;

import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.annotation.CloudJob;
import org.noear.solon.core.BeanBuilder;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.handle.Handler;

/**
 * @author noear
 * @since 1.4
 */
public class CloudJobBuilder implements BeanBuilder<CloudJob> {
    @Override
    public void doBuild(Class<?> clz, BeanWrap bw, CloudJob anno) throws Exception {
        if (Handler.class.isAssignableFrom(clz)) {
            CloudClient.job().register(anno.value(), bw.raw());
        }
    }
}
