package org.noear.solon.extend.sqltoy;

import org.noear.solon.core.Aop;
import org.noear.solon.extend.sqltoy.mapper.AbstractMapper;
import org.noear.solon.extend.sqltoy.mapper.ProxyClassBuilder;
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
    private static Map<DataSource, SqlToyLazyDao> daoMap=new HashMap<>();
    private static Map<DataSource, SqlToyCRUDService> serviceMap=new HashMap<>();
    private static Map<DataSource,Map<Class,Object>> mapperMap=new HashMap<>();
    private static Map<Class,Class> proxyClassMap=new HashMap<>();
    private static SqlToyContext context;

    public static void setContext(SqlToyContext context) {
        DbManager.context = context;
    }

    public static synchronized SqlToyLazyDao getDao(DataSource dataSource){
        SqlToyLazyDao dao = daoMap.get(dataSource);
        if(dao==null){
            SqlToyLazyDaoImpl sqlToyLazyDao=new SqlToyLazyDaoImpl();
            sqlToyLazyDao.setDataSource(dataSource);
            sqlToyLazyDao.setSqlToyContext(context);
            daoMap.put(dataSource,sqlToyLazyDao);
            dao=sqlToyLazyDao;
        }
        return dao;
    }
    public static synchronized SqlToyCRUDService getService(DataSource dataSource) {
        SqlToyCRUDService service = serviceMap.get(dataSource);
        if (service == null) {
            SqlToyCRUDServiceForSolon crudService = Aop.context().beanMake(SqlToyCRUDServiceForSolon.class).get();
            crudService.setSqlToyLazyDao(getDao(dataSource));
            serviceMap.put(dataSource,crudService);
            service=crudService;
        }
        return service;
    }
    public static synchronized  <T> T getMapper(DataSource dataSource,Class<T> type){
        T mapper=null;
        Map<Class,Object> mapSubMap=mapperMap.get(dataSource);
        if(mapSubMap==null){
            mapSubMap=new HashMap<>();
            mapperMap.put(dataSource,mapSubMap);
        }
        mapper=(T)mapSubMap.get(type);
        if(mapper==null){
           Class targetType= proxyClassMap.get(type);
           if(targetType==null){
               ProxyClassBuilder cb=new ProxyClassBuilder(context,type);
               targetType= cb.compile();
               proxyClassMap.put(type,targetType);
           }
            try {
                AbstractMapper m=(AbstractMapper) targetType.newInstance();
                m.setDao(getDao(dataSource));
                mapper=(T)m;
                mapSubMap.put(type,mapper);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return mapper;
    }

    public static Map<DataSource, SqlToyCRUDService> getServiceMap() {
        return serviceMap;
    }
}
