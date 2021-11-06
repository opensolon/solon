package org.noear.solon.extend.sqltoy;

import org.noear.solon.extend.sqltoy.annotation.Param;
import org.noear.solon.extend.sqltoy.annotation.Sql;
import org.sagacity.sqltoy.config.model.SqlToyConfig;
import org.sagacity.sqltoy.config.model.SqlType;
import org.sagacity.sqltoy.dao.SqlToyLazyDao;
import org.sagacity.sqltoy.model.Page;
import org.sagacity.sqltoy.model.QueryExecutor;
import org.sagacity.sqltoy.utils.StringUtil;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


/**
 * @author 夜の孤城
 * @since 1.5
 * */
public class MapperUtil {
    private static Map<Class<?>, Object> _proxy_cache = new HashMap<>();
    private static Object _proxy_lock = new Object();

    protected static <T> T proxy(Class<T> mapperInf, SqlToyLazyDao dao) {
        Object tmp = _proxy_cache.get(mapperInf);
        if (tmp == null) {
            synchronized (_proxy_lock) {
                tmp = _proxy_cache.get(mapperInf);
                if (tmp == null) {
                    tmp = buildProxy(mapperInf, dao);
                    _proxy_cache.put(mapperInf, tmp);
                }
            }
        }

        return (T) tmp;
    }

    /**
     * 获取代理实例
     */
    private static <T> T buildProxy(Class<?> mapperInf, SqlToyLazyDao dao) {
        return (T) Proxy.newProxyInstance(
                mapperInf.getClassLoader(),
                new Class[]{mapperInf},
                new MapperHandler(dao, mapperInf));
    }

    private static Class getGenericType(Method method) {
        Type t = method.getGenericReturnType();
        if (t instanceof ParameterizedType) {
            ParameterizedType t1 = (ParameterizedType) method.getGenericReturnType();
            return (Class) t1.getActualTypeArguments()[0];
        } else {
            return null;
        }
    }

    private static final Set<Class> primitiveTypes = new HashSet(Arrays.asList(new Class[]{
            String.class,
            Date.class,
            java.sql.Date.class,
            BigDecimal.class,
            BigInteger.class,
            Long.class,
            Integer.class,
            Byte.class,
            Double.class,
            Float.class,
            Character.class
    }));

    /**
     * 是否为原生类型，包括String,Date等
     *
     * @param retType
     * @return
     */
    private static boolean isPrimitive(Class retType) {
        if (retType == null) {
            return false;
        }
        return retType.isPrimitive() || primitiveTypes.contains(retType);
    }

    interface Invoker {
        Object invoke(Object[] arguments) throws Throwable;
    }

    static class MapperHandler implements InvocationHandler {
        protected MethodHandles.Lookup lookup;
        protected SqlToyLazyDao dao;
        protected Class<?> mapperClz;
        protected Map<Method, Invoker> invokers = new ConcurrentHashMap<>();

        protected MapperHandler(SqlToyLazyDao dao, Class<?> mapperClz) {
            this.dao = dao;
            this.mapperClz = mapperClz;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Invoker invoker = null;
            synchronized (this) {
                invoker = invokers.get(method);
                if (invoker == null) {
                    Class caller = method.getDeclaringClass();

                    if (method.isDefault()) {
                        if (this.lookup == null) {
                            Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, Integer.TYPE);
                            constructor.setAccessible(true);
                            this.lookup = constructor.newInstance(caller, MethodHandles.Lookup.PRIVATE);
                        }
                        invoker = this.lookup.unreflectSpecial(method, caller).bindTo(proxy)::invokeWithArguments;
                        invokers.put(method, invoker);
                    } else {
                        Sql sql = method.getAnnotation(Sql.class);
                        String sqlIdOrSql = method.getName();
                        if (sql != null) {
                            sqlIdOrSql = sql.value().trim();
                        }

                        SqlType sqlType = SqlType.search;
                        int spaceIndex = sqlIdOrSql.indexOf(" ");
                        if (spaceIndex == -1) {//有任意空白都认为不是ID
                            SqlToyConfig cfg = dao.getSqlToyContext().getScriptLoader().getSqlConfig(sqlIdOrSql, null, "");
                            if (cfg == null) {
                                throw new IllegalArgumentException("请检查 sqlId \"" + sqlIdOrSql + "\" 是否存在!");
                            }
                            String rawSql = cfg.getSql();
                            spaceIndex = rawSql.indexOf(" ");
                            sqlType = SqlType.getSqlType(rawSql.substring(0, spaceIndex));
                        } else {
                            sqlType = SqlType.getSqlType(sqlIdOrSql.substring(0, spaceIndex));
                        }
                        Class<?> retType = method.getReturnType();
                        Class[] paramTypes = method.getParameterTypes();
                        Parameter[] methodParameters = method.getParameters();
                        final String sqlStr = sqlIdOrSql;
                        int _pageIdx = -1;//有没有Page参数
                        int _mapParamIdx = -1;//查询参数是否用Map
                        int _objIdx = -1;
                        int _listIdx = -1;
                        Map<String,Integer> namedParamIdxMap=new HashMap<>();
                        for (int i = 0; i < paramTypes.length; i++) {
                            Class paramType=paramTypes[i];
                            if (Page.class.isAssignableFrom(paramType)) {
                                _pageIdx = i;
                            } else if (Map.class.isAssignableFrom(paramType)) {
                                _mapParamIdx = i;
                            } else if (List.class.isAssignableFrom(paramType)) {
                                _listIdx = i;
                            } else {
                                _objIdx = i;
                            }
                            Param param= methodParameters[i].getAnnotation(Param.class);
                            if(param!=null){
                                String paramName=param.value();
                                if(StringUtil.isBlank(paramName)){
                                    paramName=methodParameters[i].getName();
                                }
                                namedParamIdxMap.put(paramName,i);
                            }
                        }
                        boolean hasNamedParams=namedParamIdxMap.size()>0;
                        if(hasNamedParams){
                            _objIdx=-1;
                            _mapParamIdx=-1;
                        }
                        switch (sqlType) {
                            case search:
                                if (Page.class.isAssignableFrom(retType)) {
                                    int pageIdx = _pageIdx;
                                    int mapParamIdx = _mapParamIdx;
                                    int objIdx = _objIdx;
                                    Class entityType = getGenericType(method);
                                    boolean primitive = isPrimitive(entityType);
                                    ;
                                    if (entityType == null || primitive) {
                                        entityType = Map.class;
                                    }
                                    Class resultType = entityType;
                                    if (_objIdx > -1) {
                                        invoker = (arg) -> {
                                            Object queryEntity = arg[objIdx];
                                            Page page = pageIdx > -1 ? (Page) arg[pageIdx] : new Page();
                                            QueryExecutor queryExecutor = queryEntity == null
                                                    ? new QueryExecutor(sqlStr)
                                                    : new QueryExecutor(sqlStr, (Serializable) queryEntity);
                                            queryExecutor.resultType(resultType);
                                            Page result = dao.findPageByQuery(page, queryExecutor).getPageResult();
                                            if (primitive) {
                                                List<Map> rows = result.getRows();
                                                List distRows = rows.stream().map(it -> it.values().stream().findFirst().get()).collect(Collectors.toList());
                                                result.setRows(distRows);
                                            }
                                            return result;
                                        };
                                    } else {
                                        invoker = (arg) -> {
                                            Map<String, Object> queryMap = mapParamIdx > -1 ? (Map<String, Object>) arg[mapParamIdx] : new HashMap<>();
                                            if(hasNamedParams){
                                                for(Map.Entry<String,Integer> e:namedParamIdxMap.entrySet()){
                                                    queryMap.put(e.getKey(),arg[e.getValue()]);
                                                }
                                            }
                                            if (queryMap == null) {
                                                queryMap = new HashMap<>();
                                            }
                                            Page page = pageIdx > -1 ? (Page) arg[pageIdx] : new Page();
                                            QueryExecutor queryExecutor = new QueryExecutor(sqlStr, queryMap);
                                            queryExecutor.resultType(resultType);
                                            Page result = dao.findPageByQuery(page, queryExecutor).getPageResult();
                                            if (primitive) {
                                                List<Map> rows = result.getRows();
                                                List distRows = rows.stream().map(it -> it.values().stream().findFirst().get()).collect(Collectors.toList());
                                                result.setRows(distRows);
                                            }
                                            return result;
                                        };
                                    }
                                } else if (List.class.isAssignableFrom(retType)) {
                                    int mapParamIdx = _mapParamIdx;
                                    int objIdx = _objIdx;
                                    Class entityType = getGenericType(method);//(Class) method.getGenericReturnType();
                                    boolean primitive = isPrimitive(entityType);
                                    if (entityType == null || primitive) {
                                        entityType = Map.class;
                                    }
                                    Class resultType = entityType;
                                    if (_objIdx > -1) {
                                        invoker = (arg) -> {
                                            Object queryEntity = arg[objIdx];
                                            QueryExecutor queryExecutor = queryEntity == null
                                                    ? new QueryExecutor(sqlStr)
                                                    : new QueryExecutor(sqlStr, (Serializable) queryEntity);
                                            queryExecutor.resultType(resultType);
                                            if (primitive) {
                                                List<Map> result = dao.findByQuery(queryExecutor).getRows();
                                                return result.stream().map(it -> it.values().stream().findFirst().get()).collect(Collectors.toList());
                                            }
                                            return dao.findByQuery(queryExecutor).getRows();
                                        };
                                    } else {
                                        invoker = (arg) -> {
                                            Map<String, Object> queryMap = mapParamIdx > -1 ? (Map<String, Object>) arg[mapParamIdx] : new HashMap<>();
                                            if(hasNamedParams){
                                                for(Map.Entry<String,Integer> e:namedParamIdxMap.entrySet()){
                                                    queryMap.put(e.getKey(),arg[e.getValue()]);
                                                }
                                            }
                                            if (queryMap == null) {
                                                queryMap = new HashMap<>();
                                            }
                                            QueryExecutor queryExecutor = new QueryExecutor(sqlStr, queryMap).resultType(resultType);
                                            if (primitive) {
                                                List<Map> result = dao.findByQuery(queryExecutor).getRows();
                                                return result.stream().map(it -> it.values().stream().findFirst().get()).collect(Collectors.toList());
                                            }
                                            return dao.findByQuery(queryExecutor).getRows();
                                        };
                                    }
                                } else {
                                    if (_objIdx > -1) {
                                        int objIdx = _objIdx;
                                        invoker = (arg) -> {
                                            Object queryEntity = arg[objIdx];
                                            QueryExecutor queryExecutor = queryEntity == null
                                                    ? new QueryExecutor(sqlStr)
                                                    : new QueryExecutor(sqlStr, (Serializable) queryEntity);
                                            boolean isPrimitive = isPrimitive(retType);
                                            if (isPrimitive) {
                                                queryExecutor.resultType(Map.class);
                                            } else {
                                                queryExecutor.resultType(retType);
                                            }
                                            List result = dao.findTopByQuery(queryExecutor, 1).getRows();
                                            if (result == null || result.size() == 0) {
                                                return null;
                                            }
                                            Object target = result.get(0);
                                            return isPrimitive ? ((Map) target).values().stream().findFirst().get() : target;
                                        };
                                    } else {
                                        int mapParamIdx = _mapParamIdx;

                                        invoker = (arg) -> {
                                            Map<String, Object> queryMap = mapParamIdx > -1 ? (Map<String, Object>) arg[mapParamIdx] : new HashMap<>();
                                            if(hasNamedParams){
                                                for(Map.Entry<String,Integer> e:namedParamIdxMap.entrySet()){
                                                    queryMap.put(e.getKey(),arg[e.getValue()]);
                                                }
                                            }
                                            if (queryMap == null) {
                                                queryMap = new HashMap<>();
                                            }
                                            QueryExecutor queryExecutor = new QueryExecutor(sqlStr, queryMap);
                                            boolean isPrimitive = isPrimitive(retType);;
                                            if (isPrimitive) {
                                                queryExecutor.resultType(Map.class);
                                            } else {
                                                queryExecutor.resultType(retType);
                                            }
                                            List result = dao.findTopByQuery(queryExecutor, 1).getRows();
                                            if (result == null || result.size() == 0) {
                                                return null;
                                            }
                                            Object target = result.get(0);
                                            return isPrimitive ? ((Map) target).values().stream().findFirst().get() : target;
                                        };
                                    }

                                }
                                break;
                            case insert:
                            case delete:
                                if (_objIdx > -1) {
                                    int objIdx = _objIdx;
                                    invoker = (arg) -> {
                                        Object entity = arg[objIdx];
                                        if (entity == null) {
                                            Map<String, Object> empty = new HashMap<>();
                                            return dao.executeSql(sqlStr, empty);
                                        }
                                        return dao.executeSql(sqlStr, (Serializable) entity);
                                    };
                                } else {
                                    int mapParamIdx = _mapParamIdx;
                                    invoker = (arg) -> {
                                        Map<String, Object> queryMap = mapParamIdx > -1 ? (Map<String, Object>) arg[mapParamIdx] : new HashMap<>();
                                        if (queryMap == null) {
                                            queryMap = new HashMap<>();
                                        }
                                        return dao.executeSql(sqlStr, queryMap);
                                    };
                                }
                                break;
                            case update:
                                if (_listIdx > -1) {// batch update
                                    int listIdx = _listIdx;
                                    invoker = (arg) -> {
                                        List params = (List) arg[listIdx];
                                        if (params == null) {
                                            params = new ArrayList();
                                        }
                                        return dao.batchUpdate(sqlStr, params);
                                    };
                                } else if (_objIdx > -1) {
                                    int objIdx = _objIdx;
                                    invoker = (arg) -> {
                                        Object entity = arg[objIdx];
                                        if (entity == null) {
                                            Map<String, Object> empty = new HashMap<>();
                                            return dao.executeSql(sqlStr, empty);
                                        }
                                        return dao.executeSql(sqlStr, (Serializable) entity);
                                    };
                                } else {
                                    int mapParamIdx = _mapParamIdx;
                                    invoker = (arg) -> {
                                        Map<String, Object> queryMap = mapParamIdx > -1 ? (Map<String, Object>) arg[mapParamIdx] : new HashMap<>();
                                        if (queryMap == null) {
                                            queryMap = new HashMap<>();
                                        }
                                        return dao.executeSql(sqlStr, queryMap);
                                    };
                                }
                                break;
                            default:
                                invoker = arg -> null;
                        }
                    }
                }
            }
            // try {
            return invoker == null ? null : invoker.invoke(args);
//            }catch (Exception e){
//                Throwable real=e;
//                while (real.getCause()!=null&&real.getCause()!=real){
//                    real=real.getCause();
//                }
//                throw real;
//            }
        }
    }
}
