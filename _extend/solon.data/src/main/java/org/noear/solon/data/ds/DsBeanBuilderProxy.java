package org.noear.solon.data.ds;

import org.noear.solon.core.BeanBuilder;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.data.annotation.Ds;

/**
 * Ds构建器
 *
 * @author noear
 * @since 1.5
 */
public class DsBeanBuilderProxy implements BeanBuilder<Ds> {
    @Override
    public void doBuild(Class<?> clz, BeanWrap bw, Ds anno) throws Exception {
        for (BeanBuilder<Ds> m : DsAop.builderSet) {
            m.doBuild(clz, bw, anno);
        }
    }
}
