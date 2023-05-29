package org.noear.solon.extend.sqltoy;

import org.noear.solon.core.AopContext;
import org.sagacity.sqltoy.SqlToyContext;
import org.sagacity.sqltoy.dao.SqlToyLazyDao;
import org.sagacity.sqltoy.dao.impl.SqlToyLazyDaoImpl;
import org.sagacity.sqltoy.service.SqlToyCRUDService;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
/**
 * @author 夜の孤城
 * @since 1.5
 * */
public class DbManager {
    private static Map<DataSource, SqlToyLazyDao> daoMap = new HashMap<>();
    private static Map<DataSource, SqlToyCRUDService> serviceMap = new HashMap<>();
    private static SqlToyContext context;

    public static void setContext(SqlToyContext context) {
        DbManager.context = context;
    }

    public static synchronized SqlToyLazyDao getDao(DataSource dataSource) {
        SqlToyLazyDao dao = daoMap.get(dataSource);

        if (dao == null) {
            SqlToyLazyDaoImpl sqlToyLazyDao = new SqlToyLazyDaoImpl();
            sqlToyLazyDao.setDataSource(dataSource);
            sqlToyLazyDao.setSqlToyContext(context);
            daoMap.put(dataSource, sqlToyLazyDao);
            dao = sqlToyLazyDao;
        }

        return dao;
    }

    public static synchronized SqlToyCRUDService getService(AopContext context, DataSource dataSource) {
        SqlToyCRUDService service = serviceMap.get(dataSource);
        if (service == null) {
            SqlToyCRUDServiceForSolon crudService = context.beanMake(SqlToyCRUDServiceForSolon.class).get();
            crudService.setSqlToyLazyDao(getDao(dataSource));
            serviceMap.put(dataSource, crudService);
            service = crudService;
        }
        return service;
    }
    public static Map<DataSource, SqlToyCRUDService> getServiceMap() {
        return serviceMap;
    }
}
