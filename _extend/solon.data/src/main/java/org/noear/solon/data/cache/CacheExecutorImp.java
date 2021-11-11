package org.noear.solon.data.cache;

import org.noear.solon.Utils;
import org.noear.solon.core.wrap.ClassWrap;
import org.noear.solon.core.wrap.FieldWrap;
import org.noear.solon.core.wrap.ParamWrap;
import org.noear.solon.data.annotation.Cache;
import org.noear.solon.data.annotation.CachePut;
import org.noear.solon.data.annotation.CacheRemove;
import org.noear.solon.ext.SupplierEx;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 缓存执行器
 *
 * @author noear
 * @since 1.0
 * */
public class CacheExecutorImp {
    public static final CacheExecutorImp global = new CacheExecutorImp();

    /**
     * 添加缓存
     *
     * @param anno     注解
     * @param method   函数
     * @param params   参数
     * @param values   参数值
     * @param executor 真实执行者
     */
    public Object cache(Cache anno, Method method, ParamWrap[] params, Object[] values, SupplierEx executor) throws Throwable {
        if (anno == null) {
            return executor.get();
        }

        Map<String, Object> parMap = buildParamsMap(params, values);
        Object result = null;

        CacheService cs = CacheLib.cacheServiceGet(anno.service());

        //0.构建缓存key（如果有注解的key，优先用）
        String key = anno.key();
        if (Utils.isEmpty(key)) {
            //没有注解key，生成一个key
            key = buildCacheKey(method, parMap);
        } else {
            //格式化key
            key = formatTagsOrKey(key, parMap, null);
        }

        String keyLock = key + ":lock";

        synchronized (keyLock.intern()) {

            //1.从缓存获取
            //
            result = cs.get(key);

            if (result == null) {
                //2.执行调用，并返回
                //
                result = executor.get();

                if (result != null) {
                    //3.不为null，则进行缓存
                    //
                    cs.store(key, result, anno.seconds());

                    if (Utils.isNotEmpty(anno.tags())) {
                        String tags = formatTagsOrKey(anno.tags(), parMap, result);
                        CacheTags ct = new CacheTags(cs);

                        //4.添加缓存标签
                        for (String tag : tags.split(",")) {
                            ct.add(tag, key);
                        }
                    }
                }
            }

            return result;
        }
    }

    /**
     * 清除移除
     *
     * @param anno   注解
     * @param method 函数
     * @param params 参数
     * @param values 参数值
     */
    public void cacheRemove(CacheRemove anno, Method method, ParamWrap[] params, Object[] values) {
        if (anno == null) {
            return;
        }

        CacheService cs = CacheLib.cacheServiceGet(anno.service());
        Map<String, Object> parMap = buildParamsMap(params, values);


        //按 tags 清除缓存
        if (Utils.isNotEmpty(anno.tags())) {
            String tags = formatTagsOrKey(anno.tags(), parMap, null);
            CacheTags ct = new CacheTags(cs);

            for (String tag : tags.split(",")) {
                ct.remove(tag);
            }
        }

        //按 key 清除缓存
        if (Utils.isNotEmpty(anno.key())) {
            String key = formatTagsOrKey(anno.key(), parMap, null);
            cs.remove(key);
        }
    }

    /**
     * 缓存更新
     *
     * @param anno     注解
     * @param method   函数
     * @param params   参数
     * @param values   参数值
     * @param newValue 新的值
     */
    public void cachePut(CachePut anno, Method method, ParamWrap[] params, Object[] values, Object newValue) {
        if (anno == null) {
            return;
        }

        CacheService cs = CacheLib.cacheServiceGet(anno.service());
        Map<String, Object> parMap = buildParamsMap(params, values);

        //按 tags 更新缓存
        if (Utils.isNotEmpty(anno.tags())) {
            String tags = formatTagsOrKey(anno.tags(), parMap, newValue);
            CacheTags ct = new CacheTags(cs);

            for (String tag : tags.split(",")) {
                ct.update(tag, newValue, anno.seconds());
            }
        }

        //按 key 更新缓存
        if (Utils.isNotEmpty(anno.key())) {
            String key = formatTagsOrKey(anno.key(), parMap, newValue);
            cs.store(key, newValue, anno.seconds());
        }
    }

    /**
     * 构建参数为Map
     */
    protected Map<String, Object> buildParamsMap(ParamWrap[] params, Object[] values) {
        Map<String, Object> parMap = new LinkedHashMap<>();

        for (int i = 0, len = params.length; i < len; i++) {
            parMap.put(params[i].getName(), values[i]);
        }

        return parMap;
    }


    /**
     * 构建缓存Key
     */
    protected String buildCacheKey(Method method, Map<String, Object> parMap) {
        StringBuilder keyB = new StringBuilder();

        keyB.append(method.getDeclaringClass().getName()).append(":");
        keyB.append(method.getName()).append(":");

        parMap.forEach((k, v) -> {
            keyB.append(k).append("_").append(v);
        });

        return keyB.toString();
    }

    protected String formatTagsOrKey(String str, Map map, Object rst) {
        if (str.indexOf("$") < 0) {
            return str;
        }

        String str2 = str;

        Pattern pattern = Pattern.compile("\\$\\{(\\.?\\w+)\\}");
        Matcher m = pattern.matcher(str);
        while (m.find()) {
            String mark = m.group(0);
            String name = m.group(1);

            if (map.containsKey(name)) {
                //说明从输入参数取值
                String val = String.valueOf(map.get(name));

                str2 = str2.replace(mark, val);
            } else if (name.startsWith(".")) {
                //说明要从返回结果取值
                String val = null;
                if (rst != null) {
                    FieldWrap fw = ClassWrap.get(rst.getClass()).getFieldWrap(name.substring(1));
                    if (fw == null) {
                        throw new IllegalArgumentException("Missing cache tag parameter (result field): " + name);
                    }

                    try {
                        Object val2 = fw.getValue(rst);
                        if (val2 != null) {
                            val = val2.toString();
                        }
                    } catch (ReflectiveOperationException e) {
                        throw new RuntimeException(e);
                    }
                }

                if (val == null) {
                    val = "null";
                }

                str2 = str2.replace(mark, val);
            } else {
                //如果缺少参数就出异常，容易发现问题
                throw new IllegalArgumentException("Missing cache tag parameter: " + name);
            }
        }

        return str2;
    }
}
