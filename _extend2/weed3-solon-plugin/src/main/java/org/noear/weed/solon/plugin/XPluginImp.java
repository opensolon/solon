package org.noear.weed.solon.plugin;

import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.core.*;
import org.noear.weed.BaseMapper;
import org.noear.weed.DbContext;
import org.noear.weed.WeedConfig;
import org.noear.weed.annotation.Db;
import org.noear.weed.xml.XmlSqlLoader;

import javax.sql.DataSource;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        //
        // 事件监听，用于时实初始化
        //
        app.onEvent(BeanWrap.class, new DsEventListener());

        //
        // 切换Weed的链接工厂，交于Solon托管这
        //
        WeedConfig.connectionFactory = new DsConnectionFactoryImpl();

        Aop.factory().beanCreatorAdd(Db.class, (clz, cbw, anno) -> {
            if (clz.isInterface() == false) {
                return;
            }

            if (XUtil.isEmpty(anno.value())) {
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

        Aop.factory().beanInjectorAdd(Db.class, (varH, anno) -> {
            if (XUtil.isEmpty(anno.value())) {
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

        //加载xml sql
        XmlSqlLoader.tryLoad();
    }

    private void create0(Class<?> clz, BeanWrap dsBw) {
        DbContext db = DbManager.global().get(dsBw);
        Aop.wrapAndPut(clz, db.mapper(clz));
    }

    private void inject0(VarHolder varH, BeanWrap dsBw) {
        DbContext db = DbManager.global().get(dsBw);
        Class<?> clz = varH.getType();

        if (DbContext.class.isAssignableFrom(clz)) {
            varH.setValue(db);
        } else if (clz.isInterface()) {
            if (clz == BaseMapper.class) {
                Object obj = db.mapperBase((Class<?>) varH.getGenericType().getActualTypeArguments()[0]);
                varH.setValue(obj);
            } else {
                Object obj = db.mapper(clz);
                varH.setValue(obj);
            }
        }
    }
}
