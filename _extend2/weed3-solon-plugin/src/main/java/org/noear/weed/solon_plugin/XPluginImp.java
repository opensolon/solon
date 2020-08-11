package org.noear.weed.solon_plugin;

import org.noear.solon.XApp;
import org.noear.solon.core.*;
import org.noear.weed.BaseMapper;
import org.noear.weed.DbContext;
import org.noear.weed.annotation.Db;
import org.noear.weed.xml.XmlSqlLoader;

import java.util.function.Consumer;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
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
        Aop.getAsyn(anno.value(), (bw) -> {
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
        });
    }
}
