package org.noear.solon.extend.powerjob;

import org.noear.solon.core.BeanBuilder;
import org.noear.solon.core.BeanWrap;
import tech.powerjob.solon.annotation.PowerJob;

/**
 * @author noear
 * @since 2.0
 */
public class PowerjobBeanBuilder implements BeanBuilder<PowerJob> {
    @Override
    public void doBuild(Class<?> clz, BeanWrap bw, PowerJob anno) throws Throwable {
        //不用做什么
    }
}
