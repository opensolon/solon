package org.noear.weed.solon.plugin;

import org.noear.solon.Utils;
import org.noear.solon.core.*;
import org.noear.weed.BaseMapper;
import org.noear.weed.DbContext;
import org.noear.weed.annotation.Db;

import javax.sql.DataSource;

/**
 * @author noear
 * @since 1.3
 */
public class DbBeanReactor implements BeanBuilder<Db>, BeanInjector<Db> {
    @Override
    public void doBuild(Class<?> clz, BeanWrap wrap, Db anno) throws Exception {
        if (clz.isInterface() == false) {
            return;
        }

        if (Utils.isEmpty(anno.value())) {
            wrap.context().getWrapAsync(DataSource.class, (dsBw) -> {
                create0(clz, dsBw);
            });
        } else {
            wrap.context().getWrapAsync(anno.value(), (dsBw) -> {
                if (dsBw.raw() instanceof DataSource) {
                    create0(clz, dsBw);
                }
            });
        }
    }

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


    private void create0(Class<?> clz, BeanWrap dsBw) {
        DbContext db = DbManager.global().get(dsBw);
        dsBw.context().wrapAndPut(clz, db.mapper(clz));
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
