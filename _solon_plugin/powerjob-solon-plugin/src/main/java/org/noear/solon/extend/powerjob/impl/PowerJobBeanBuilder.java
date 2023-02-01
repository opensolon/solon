package org.noear.solon.extend.powerjob.impl;

import org.noear.solon.Utils;
import org.noear.solon.core.BeanBuilder;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.extend.powerjob.JobBeanManager;
import tech.powerjob.solon.annotation.PowerJob;

/**
 * @author noear
 * @since 2.0
 */
public class PowerJobBeanBuilder implements BeanBuilder<PowerJob> {
    @Override
    public void doBuild(Class<?> clz, BeanWrap bw, PowerJob anno) throws Throwable {
        JobBeanManager.addJob(clz.getName(), bw);

        if (Utils.isNotEmpty(anno.value())) {
            JobBeanManager.addJob(anno.value(), bw);
        }
    }
}
