package org.noear.weed.solon.plugin;

import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.core.*;
import org.noear.weed.BaseMapper;
import org.noear.weed.DbContext;
import org.noear.weed.annotation.Db;
import org.noear.weed.xml.XmlSqlLoader;

import java.util.function.Consumer;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        //XBridge.tranFactorySet(TranFactoryImp.singleton());

        XBridge.tranSessionFactorySet(new TranSessionFactoryImp());

        Aop.factory().beanCreatorAdd(Db.class, (clz, bw, anno) -> {
            if (clz.isInterface()) {
                getMapper(clz, anno, null, (raw) -> {
                    Aop.wrapAndPut(clz, raw);
                });
            }
        });

        Aop.factory().beanInjectorAdd(Db.class, (fwT, anno) -> {
            if (fwT.getType().isInterface()) {
                getMapper(fwT.getType(), anno, fwT, (raw) -> {
                    fwT.setValue(raw);
                });
            }
        });

        //加载xml sql
        XmlSqlLoader.tryLoad();
    }

    public void getMapper(Class<?> clz, Db anno, VarHolder varH, Consumer<Object> callback) {
        if (XUtil.isEmpty(anno.value())) {
            //根据类型（用于支持主配置的概念）
            Aop.getAsyn(DbContext.class, (bw) -> {
                getMapper0(clz, bw, varH, callback);
            });
        } else {
            //根据名字
            Aop.getAsyn(anno.value(), (bw) -> {
                getMapper0(clz, bw, varH, callback);
            });
        }
    }

    private void getMapper0(Class<?> clz, BeanWrap bw, VarHolder varH, Consumer<Object> callback) {
        DbContext db = bw.raw();
        Object obj = null;
        //生成mapper
        if (varH != null && varH.getGenericType() != null) {
            if (clz == BaseMapper.class) {
                obj = db.mapperBase((Class<?>) varH.getGenericType().getActualTypeArguments()[0]);
            }
        } else {
            obj = db.mapper(clz);
        }

        if (obj != null) {
            callback.accept(obj);
        }
    }
}
