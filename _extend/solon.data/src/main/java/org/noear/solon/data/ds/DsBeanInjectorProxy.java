package org.noear.solon.data.ds;

import org.noear.solon.core.BeanInjector;
import org.noear.solon.core.VarHolder;
import org.noear.solon.data.annotation.Ds;

/**
 * Ds 注入器
 *
 * @author noear
 * @since 1.5
 */
public class DsBeanInjectorProxy implements BeanInjector<Ds> {
    @Override
    public void doInject(VarHolder varH, Ds anno) {
        for (BeanInjector<Ds> m : DsAop.injectorSet) {
            m.doInject(varH, anno);
        }
    }
}
