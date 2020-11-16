package org.noear.solon.extend.beetlsql;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.ext.solon.Db;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.core.Aop;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.VarHolder;
import org.noear.solon.core.Plugin;

import javax.sql.DataSource;

/**
 * Solon 插件接口实现，完成对接与注入支持
 *
 * @author noear
 * @since 2020-09-01
 * */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        //监听事件
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
                    inject0(anno, varH, dsBw);
                });
            } else {
                Aop.getAsyn(anno.value(), (dsBw) -> {
                    if (dsBw.raw() instanceof DataSource) {
                        inject0(anno, varH, dsBw);
                    }
                });
            }
        });

        //初始化管理器（主要为了生成动态管理器）
        //
        Aop.context().beanOnloaded(() -> {
            BeanWrap defBw = Aop.context().getWrap(DataSource.class);
            DbManager.global().dynamicBuild(defBw);

            Aop.wrapAndPut(SQLManager.class, DbManager.global().dynamicGet());
        });
    }

    private void create0(Class<?> clz, BeanWrap dsBw) {
        Object raw = DbManager.global().get(dsBw).getMapper(clz);
        Aop.wrapAndPut(clz, raw);
    }

    /**
     * 字段注入
     */
    private void inject0(Db anno, VarHolder varH, BeanWrap dsBw) {
        SQLManager tmp = DbManager.global().get(dsBw);

        if (varH.getType().isInterface()) {
            Object mapper = tmp.getMapper(varH.getType());

            varH.setValue(mapper);
            return;
        }

        if (SQLManager.class.isAssignableFrom(varH.getType())) {
            if (Utils.isNotEmpty(anno.value())) {
                varH.setValue(tmp);
            } else {
                Aop.getAsyn(SQLManager.class, (bw2) -> {
                    varH.setValue(bw2.raw());
                });
            }
            return;
        }
    }
}
