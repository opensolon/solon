package org.noear.solon.extend.activerecord.impl;

import com.jfinal.plugin.activerecord.Model;
import org.noear.solon.core.BeanBuilder;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.extend.activerecord.ModelManager;
import org.noear.solon.extend.activerecord.annotation.Table;

/**
 * @author noear
 * @since 1.10
 */
public class TableBeanBuilderImpl implements BeanBuilder<Table> {
    @Override
    public void doBuild(Class<?> clz, BeanWrap bw, Table anno) throws Throwable {
        if (!(bw.raw() instanceof Model)) {
            return;
        }

        ModelManager.addModel(anno, bw.raw());
    }
}
