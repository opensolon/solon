package org.hibernate.solon.integration;

import org.hibernate.solon.annotation.Db;
import org.noear.solon.Utils;
import org.noear.solon.core.BeanInjector;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.VarHolder;

import javax.sql.DataSource;

/**
 * @author lingkang
 * @since 2.5
 */
public class DbBeanInjector implements BeanInjector<Db> {
    @Override
    public void doInject(VarHolder varH, Db anno) {
        if (Utils.isEmpty(anno.value())) {
            varH.context().getWrapAsync(DataSource.class, (dsBw) -> {
                inject0(varH, dsBw);
            });
        } else {
            varH.context().getWrapAsync(anno.value(), (dsBw) -> {
                if (dsBw.raw() instanceof DataSource) {
                    inject0(varH, dsBw);
                }
            });
        }
    }

    private void inject0(VarHolder varH, BeanWrap dsBw) {
        HibernateAdapter adapter = HibernateAdapterManager.get(dsBw);

        if (adapter != null) {
            adapter.injectTo(varH);
        }
    }
}
