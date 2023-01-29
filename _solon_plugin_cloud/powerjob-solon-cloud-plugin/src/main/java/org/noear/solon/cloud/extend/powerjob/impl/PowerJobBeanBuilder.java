package org.noear.solon.cloud.extend.powerjob.impl;

import org.noear.solon.Utils;
import org.noear.solon.cloud.annotation.CloudJob;
import org.noear.solon.cloud.extend.powerjob.JobBeanManager;
import org.noear.solon.core.BeanBuilder;
import org.noear.solon.core.BeanWrap;

/**
 * @author noear
 * @since 2.0
 */
public class PowerJobBeanBuilder implements BeanBuilder<CloudJob> {
    @Override
    public void doBuild(Class<?> clz, BeanWrap bw, CloudJob anno) throws Throwable {
        JobBeanManager.addJob(clz.getName(), bw);

        String name = Utils.annoAlias(anno.value(), anno.name());

        if (Utils.isNotEmpty(name)) {
            JobBeanManager.addJob(name, bw);
        }
    }
}
