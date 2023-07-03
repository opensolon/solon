package org.noear.solon.extend.sqltoy;

import org.noear.solon.core.BeanInjector;
import org.noear.solon.core.VarHolder;
import org.noear.solon.extend.sqltoy.annotation.Db;
import org.sagacity.sqltoy.SqlToyContext;
import org.sagacity.sqltoy.dao.LightDao;
import org.sagacity.sqltoy.dao.SqlToyLazyDao;
import org.sagacity.sqltoy.service.SqlToyCRUDService;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 夜の孤城
 * @since 1.5
 * */
class DbInjector implements BeanInjector<Db> {
    private static Map<DataSource, SqlToyLazyDao> daoMap = new ConcurrentHashMap();
    private static Map<DataSource, SqlToyCRUDService> serviceMap = new ConcurrentHashMap();

    @Override
    public void doInject(VarHolder varH, Db anno) {
        String v = anno.value();
        if (v.equals("")) {
            varH.context().getWrapAsync(DataSource.class, bw -> {
                inject(bw.get(), varH);
            });
        } else {
            varH.context().getWrapAsync(v, bw -> {
                inject(bw.get(), varH);
            });
        }
    }

    private void inject(DataSource dataSource, VarHolder varH) {
        Class type = varH.getType();

        if (type.equals(DataSource.class)) {
            varH.setValue(dataSource);
            return;
        }

        varH.context().getWrapAsync(SqlToyContext.class, bw -> {
            if (type.equals(SqlToyLazyDao.class)) {
                varH.setValue(DbManager.getDao(dataSource));
                return;
            }
            if(type.equals(LightDao.class)){
                varH.setValue(DbManager.getLightDao(dataSource));
                return;
            }
            if (type.equals(SqlToyCRUDService.class)) {
                varH.setValue(DbManager.getService(bw.context(), dataSource));
                return;
            }

//            if (type.isInterface()) {
//                varH.setValue(DbManager.getMapper(dataSource, type));
//                return;
//            }
        });
    }
}
