package org.noear.solon.extend.mybatis;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.core.*;
import org.noear.solon.extend.mybatis.annotation.Db;

import javax.sql.DataSource;

public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        app.onEvent(BeanWrap.class, new DsEventListener());

        Aop.context().beanBuilderAdd(Db.class, (clz, wrap, anno) -> {
            if (clz.isInterface() == false) {
                return;
            }

            if (Utils.isEmpty(anno.value())) {
                Aop.getAsyn(DataSource.class, (dsBw) -> {
                    create0(clz, dsBw);
                });
            } else {
                Aop.getAsyn(anno.value(), (dsBw) -> {
                    if (dsBw.raw() instanceof DataSource) {
                        create0(clz, dsBw);
                    }
                });
            }
        });

        Aop.context().beanInjectorAdd(Db.class, (varH, anno) -> {
            if (Utils.isEmpty(anno.value())) {
                Aop.getAsyn(DataSource.class, (dsBw) -> {
                    inject0(varH, dsBw);
                });
            } else {
                Aop.getAsyn(anno.value(), (dsBw) -> {
                    if (dsBw.raw() instanceof DataSource) {
                        inject0(varH, dsBw);
                    }
                });
            }
        });
    }

    private void create0(Class<?> clz, BeanWrap dsBw) {
        SqlSessionHolder holder = DbManager.global().get(dsBw);

        Object raw = holder.getMapper(clz);
        Aop.wrapAndPut(clz,raw);
    }

    private void inject0(VarHolder varH, BeanWrap dsBw) {
        SqlSessionHolder holder = DbManager.global().get(dsBw);

        if (varH.getType().isInterface()) {
            Object mapper = holder.getMapper(varH.getType());

            varH.setValue(mapper);
            return;
        }

        if (SqlSession.class.isAssignableFrom(varH.getType())) {
            varH.setValue(holder);
            return;
        }

        if (SqlSessionFactory.class.isAssignableFrom(varH.getType())) {
            varH.setValue(holder.getFactory());
            return;
        }
    }
}
