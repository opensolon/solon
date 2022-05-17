package org.noear.solon.extend.mybatis.integration;

import org.apache.ibatis.session.SqlSession;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.core.*;
import org.apache.ibatis.ext.solon.Db;
import org.noear.solon.extend.mybatis.MybatisAdapter;

import javax.sql.DataSource;

public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {

        app.onEvent(BeanWrap.class, new DsEventListener());

        //for @Deprecated
        Aop.context().beanBuilderAdd(org.apache.ibatis.ext.solon.Db.class, (clz, wrap, anno) -> {
            builderAddDo(clz, wrap,anno.value());
        });

        Aop.context().beanInjectorAdd(org.apache.ibatis.ext.solon.Db.class, (varH, anno) -> {
            injectorAddDo(varH, anno.value());
        });

        //for new
        Aop.context().beanBuilderAdd(org.apache.ibatis.solon.annotation.Db.class, (clz, wrap, anno) -> {
            builderAddDo(clz, wrap,anno.value());
        });

        Aop.context().beanInjectorAdd(org.apache.ibatis.solon.annotation.Db.class, (varH, anno) -> {
            injectorAddDo(varH, anno.value());
        });
    }

    private void builderAddDo(Class<?> clz, BeanWrap wrap, String annoValue) {
        if (clz.isInterface() == false) {
            return;
        }

        if (Utils.isEmpty(annoValue)) {
            wrap.context().getWrapAsyn(DataSource.class, (dsBw) -> {
                create0(clz, dsBw);
            });
        } else {
            wrap.context().getWrapAsyn(annoValue, (dsBw) -> {
                if (dsBw.raw() instanceof DataSource) {
                    create0(clz, dsBw);
                }
            });
        }
    }

    private void injectorAddDo(VarHolder varH, String annoValue) {
        if (Utils.isEmpty(annoValue)) {
            varH.context().getWrapAsyn(DataSource.class, (dsBw) -> {
                inject0(varH, dsBw);
            });
        } else {
            varH.context().getWrapAsyn(annoValue, (dsBw) -> {
                if (dsBw.raw() instanceof DataSource) {
                    inject0(varH, dsBw);
                }
            });
        }
    }


    private void create0(Class<?> clz, BeanWrap dsBw) {
        SqlSession session = MybatisAdapterManager.get(dsBw).getFactory().openSession();

        Object raw = session.getMapper(clz);
        dsBw.context().wrapAndPut(clz, raw);
    }

    private void inject0(VarHolder varH, BeanWrap dsBw) {
        MybatisAdapter adapter = MybatisAdapterManager.get(dsBw);

        if (adapter != null) {
            adapter.injectTo(varH);
        }
    }
}
