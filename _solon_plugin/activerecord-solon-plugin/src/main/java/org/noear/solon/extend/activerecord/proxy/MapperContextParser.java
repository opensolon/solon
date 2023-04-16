package org.noear.solon.extend.activerecord.proxy;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.noear.solon.Utils;
import org.noear.solon.core.util.GenericUtil;
import org.noear.solon.core.util.ReflectUtil;
import org.noear.solon.extend.activerecord.annotation.Namespace;
import org.noear.solon.extend.activerecord.annotation.Sql;

import com.jfinal.plugin.activerecord.Page;

/**
 * Mapper 接口解析器
 *
 * @author 胡高 (https://gitee.com/gollyhu)
 * @since 1.10
 */
public class MapperContextParser {
    private static Class<?> getReturnClass(Method method, boolean isReturnList, boolean isReturnPage) {
        if (isReturnList || isReturnPage) {
            // 获取返回泛型类型
            ParameterizedType parameterizedType = GenericUtil.toParameterizedType(method.getGenericReturnType());
            if (parameterizedType == null) {
                return null;
            }

            // 返回泛型的的第一个Class类型对象
            if (parameterizedType.getActualTypeArguments().length > 0) {
                return (Class<?>)parameterizedType.getActualTypeArguments()[0];
            } else {
                return null;
            }
        }

        return method.getReturnType();
    }

    /**
     *
     * @param method
     * @return
     */
    private static String getSql(Class<?> clz, Method method, boolean isSqlStatement) {
        if (isSqlStatement) {
            String sql = method.getAnnotation(Sql.class).value().trim();
            return sql.substring(1, sql.length() - 1).trim();
        } else {
            // 名称空间
            String namespace = null;
            String sql = method.getName(); // 在没有@Sql注解的情况下，sql名默认为方法名

            /*
             * 从类上获取@Namespace注解
             */
            Namespace namespaceAnno = clz.getAnnotation(Namespace.class);
            if (null != namespaceAnno) {
                namespace = namespaceAnno.value();
            }

            /*
             * 从方法上获取@Namespace注解
             */
            namespaceAnno = method.getAnnotation(Namespace.class);
            if (null != namespaceAnno) {
                // 方法上的@Namespace注解将覆盖掉类上的@namespace注解
                namespace = namespaceAnno.value();
            }

            /*
             * 从方法上获取@Sql注解
             */
            Sql sqlAnno = method.getAnnotation(Sql.class);
            if (null != sqlAnno) {
                sql = sqlAnno.value();
            }

            /*
             * 拼装Sql模板名
             */
            return (Utils.isNotBlank(namespace) ? namespace + '.' : "") + sql;
        }
    }

    /**
     * 是否为SQL语句
     *
     * @param method
     *            方法实例
     * @return true 是， false 否
     */
    private static boolean isSqlStatement(Method method) {
        Sql sqlAnno = method.getAnnotation(Sql.class);
        if (null == sqlAnno) {
            return false;
        }

        String sql = sqlAnno.value().trim();

        return sql.startsWith("{") && sql.endsWith("}");
    }

    private static boolean isSqlUpdate(Method method) {
        Sql sqlAnno = method.getAnnotation(Sql.class);
        if (null == sqlAnno) {
            return false;
        }

        return sqlAnno.isUpdate();
    }

    /**
     * 解析类
     *
     * @param clz
     *            类Class
     */
    public static void parse(Class<?> clz) {
        Map<Method, MapperMethodContext> contextMap = MapperMethodContextManager.getContextMap(clz);
        if (null != contextMap) {
            return;
        }

        contextMap = new HashMap<>();

        /*
         * 循环解析所有方法
         */
        for (Method method : ReflectUtil.getDeclaredMethods(clz)) {
            MapperMethodContext methodContext = parseDaoMethod(clz, method);
            contextMap.put(method, methodContext);

            // 缓存方法的Context到Manager
            MapperMethodContextManager.setMethodContext(method, methodContext);
        }

        // 缓存类的Context Map到Manager
        MapperMethodContextManager.setContextMap(clz, contextMap);
    }

    /**
     * 解析Dao方法
     *
     * @param clz
     *            所属类的Class
     * @param method
     *            方法实例
     * @return 方法的Context对象
     */
    private static MapperMethodContext parseDaoMethod(Class<?> clz, Method method) {
        MapperMethodContext context = MapperMethodContextManager.getMethodContext(method);
        if (null != context) {
            return context;
        }

        // 解析方法中的Context相关信息
        boolean isReturnList = Collection.class.isAssignableFrom(method.getReturnType());
        boolean isReturnPage = Page.class.isAssignableFrom(method.getReturnType());
        boolean isSqlStatement = isSqlStatement(method);
        boolean isUpdate = isSqlUpdate(method);
        Class<?> returnClass = getReturnClass(method, isReturnList, isReturnPage);

        /*
         * 创建Context对象
         */
        context = new MapperMethodContext(getSql(clz, method, isSqlStatement), method.getParameters(), returnClass,
            isReturnList, isReturnPage, isSqlStatement, isUpdate);

        return context;
    }

}
