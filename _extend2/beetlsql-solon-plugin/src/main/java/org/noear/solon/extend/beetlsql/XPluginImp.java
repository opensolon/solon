package org.noear.solon.extend.beetlsql;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.ext.solon.Db;
import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.core.Aop;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.VarHolder;
import org.noear.solon.core.XPlugin;

import javax.sql.DataSource;

/**
 * Solon 插件接口实现，完成对接与注入支持
 *
 * @author noear
 * @since 2020-09-01
 * */
public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        app.onEvent(BeanWrap.class,new DsEventListener());

        //构建bean
        //
        Aop.factory().beanCreatorAdd(Db.class, (clz, wrap, anno) -> {
            if (XUtil.isEmpty(anno.value())) {
                Aop.getAsyn(DataSource.class, (bw) -> {
                    if (clz.isInterface()) {
                        Object raw = DbManager.global().get(anno.value(), bw).getMapper(clz);
                        Aop.wrapAndPut(clz, raw);
                    }
                });
            } else {
                Aop.getAsyn(anno.value(), (bw) -> {
                    if (bw.raw() instanceof DataSource && clz.isInterface()) {
                        Object raw = DbManager.global().get(anno.value(), bw).getMapper(clz);
                        Aop.wrapAndPut(clz, raw);
                    }
                });
            }
        });

        //注入bean
        //
        Aop.factory().beanInjectorAdd(Db.class, (varH, anno) -> {

            if (XUtil.isEmpty(anno.value())) {
                Aop.getAsyn(DataSource.class, (bw) -> {
                    injectDo(anno, bw, varH);
                });
            } else {
                Aop.getAsyn(anno.value(), (bw) -> {
                    if (bw.raw() instanceof DataSource) {
                        injectDo(anno, bw, varH);
                    }
                });
            }
        });

        //初始化管理器（主要为了生成动态管理器）
        //
        Aop.beanOnloaded(() -> {
            //初始化所有 DataSource 对应的管理器
            Aop.beanForeach((k, bw) -> {
                if (bw.raw() instanceof DataSource) {
                    DbManager.global().get(k, bw);
                }
            });

            BeanWrap defBw = Aop.factory().getWrap(DataSource.class);
            DbManager.global().dynamicBuild(defBw);

            Aop.wrapAndPut(SQLManager.class, DbManager.global().dynamicGet());
        });
    }

    /**
     * 字段注入
     */
    private void injectDo(Db anno, BeanWrap bw, VarHolder varH) {
        SQLManager tmp = DbManager.global().get(anno.value(), bw);

        if (varH.getType().isInterface()) {
            Object mapper = tmp.getMapper(varH.getType());

            varH.setValue(mapper);
            return;
        }

        if (SQLManager.class.isAssignableFrom(varH.getType())) {
            if (XUtil.isNotEmpty(anno.value())) {
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
