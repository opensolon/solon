package org.noear.solon.extend.activerecord.proxy;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.jfinal.plugin.activerecord.Model;

/**
 * Dao 方法上下文管理器
 *
 * @author 胡高 (https://gitee.com/gollyhu)
 * @since 1.10.7
 */
public class MapperMethodContextManager {

    static Map<Class<? extends Model<?>>, Map<Method, MapperMethodContext>> DAO_METHOD_CONTEXT_MAP = new LinkedHashMap<>();

    static Map<Method, MapperMethodContext> METHOD_CONTEXT_MAP = new HashMap<>();

    public static Map<Method, MapperMethodContext> getContextMap(Class<? extends Model<?>> clz) {
        return DAO_METHOD_CONTEXT_MAP.get(clz);
    }

    public static MapperMethodContext getMethodContext(Method method) {
        return METHOD_CONTEXT_MAP.get(method);
    }

    public static void setContextMap(Class<? extends Model<?>> clz, Map<Method, MapperMethodContext> map) {
        DAO_METHOD_CONTEXT_MAP.put(clz, map);
    }

    public static void setMethodContext(Method method, MapperMethodContext context) {
        METHOD_CONTEXT_MAP.put(method, context);
    }
}
