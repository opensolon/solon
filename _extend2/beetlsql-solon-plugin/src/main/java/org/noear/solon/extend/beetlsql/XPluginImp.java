package org.noear.solon.extend.beetlsql;

import org.beetl.sql.core.SQLManager;
import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XPlugin;

import javax.sql.DataSource;

/**
 * Solon 插件接口实现，完成对接与注入支持
 *
 * @author noear
 * */
public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        Aop.factory().beanCreatorAdd(org.beetl.sql.ext.solon.Db.class, (clz, wrap, anno)->{
            if(XUtil.isEmpty(anno.value()) || clz.isInterface() == false){
                return;
            }

            Aop.getAsyn(anno.value(),(bw)->{
                if (bw.raw() instanceof DataSource) {
                    DataSource source = bw.raw();

                    Object raw = org.beetl.sql.ext.solon.SQLManagerHolder.get(source).getMapper(clz);
                    Aop.wrapAndPut(clz,raw);
                }
            });
        });

        Aop.factory().beanInjectorAdd(org.beetl.sql.ext.solon.Db.class, (varH, anno) -> {

            if (XUtil.isEmpty(anno.value())) {
                if (varH.getType().isInterface()) {
                    Aop.getAsyn(varH.getType(), (bw) -> {
                        varH.setValue(bw.raw());
                    });
                }
            } else {
                Aop.getAsyn(anno.value(), (bw) -> {
                    if (bw.raw() instanceof DataSource) {
                        DataSource source = bw.raw();

                        org.beetl.sql.ext.solon.SQLManagerHolder holder = org.beetl.sql.ext.solon.SQLManagerHolder.get(source);

                        if (varH.getType().isInterface()) {
                            Object mapper = holder.getMapper(varH.getType());

                            varH.setValue(mapper);
                            return;
                        }

                        if (SQLManager.class.isAssignableFrom(varH.getType())) {
                            varH.setValue(holder.sqlManager);
                            return;
                        }
                    }
                });
            }
        });
    }
}
