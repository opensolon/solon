package org.hibernate.solon.integration;

import org.hibernate.solon.annotation.Db;
import org.hibernate.solon.integration.HibernateAdapter;
import org.hibernate.solon.integration.HibernateAdapterManager;
import org.noear.solon.Utils;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.VarHolder;

import javax.sql.DataSource;

/**
 * @author noear
 * @since 2.5
 */
public class HibernatePluginImpl implements Plugin {
    @Override
    public void start(AppContext context) throws Throwable {
        context.subWrapsOfType(DataSource.class, bw -> {
            HibernateAdapterManager.register(bw);
        });

        context.beanInjectorAdd(Db.class, (varH, anno) -> {
            injectorAddDo(varH, anno.value());
        });
    }

    private void injectorAddDo(VarHolder varH, String annoValue) {
        if (Utils.isEmpty(annoValue)) {
            varH.context().getWrapAsync(DataSource.class, (dsBw) -> {
                //inject0(varH, dsBw);
            });
        } else {
            varH.context().getWrapAsync(annoValue, (dsBw) -> {
                if (dsBw.raw() instanceof DataSource) {
                    //inject0(varH, dsBw);
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
