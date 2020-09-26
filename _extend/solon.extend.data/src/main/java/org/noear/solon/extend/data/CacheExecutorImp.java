package org.noear.solon.extend.data;

import org.noear.solon.XUtil;
import org.noear.solon.annotation.XCache;
import org.noear.solon.annotation.XCacheRemove;
import org.noear.solon.core.CacheService;
import org.noear.solon.core.XBridge;
import org.noear.solon.core.XCacheExecutor;
import org.noear.solon.ext.SupplierEx;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 缓存执行器
 * */
public class CacheExecutorImp implements XCacheExecutor {
    public static final CacheExecutorImp global = new CacheExecutorImp();

    /**
     * 添加缓存
     * */
    @Override
    public Object cache(XCache anno, Method method, Parameter[] params, Object[] values, SupplierEx callable) throws Throwable {
        if (anno == null) {
            return callable.get();
        }

        Map<String, Object> parMap = new HashMap<>();
        Object result = null;

        CacheService cs = XBridge.cacheServiceGet(anno.service());

        //0.构建缓存key
        String key = buildKey(method, params, values, parMap);

        //1.从缓存获取
        //
        if (anno.get()) {
            result = cs.get(key);
        }

        if (result == null) {
            //2.执行调用，并返回
            //
            result = callable.get();

            if (result != null && anno.put()) {
                //3.不为null，则进行缓存
                //
                cs.store(key, result, anno.seconds());

                if (XUtil.isNotEmpty(anno.tags())) {
                    String tags = formatTags(anno.tags(), parMap);
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

    /**
     * 清除缓存标签
     * */
    @Override
    public void cacheRemove(XCacheRemove anno, Method method, Parameter[] params, Object[] values) {
        if (anno == null || XUtil.isEmpty(anno.tags())) {
            return;
        }

        CacheService cs = XBridge.cacheServiceGet(anno.service());
        Map<String, Object> parMap = new HashMap<>();
        for (int i = 0, len = params.length; i < len; i++) {
            parMap.put(params[i].getName(), values[i]);
        }


        String tags = formatTags(anno.tags(), parMap);
        CacheTags ct = new CacheTags(cs);

        //清除缓存
        for (String tag : tags.split(",")) {
            ct.clear(tag);
        }
    }


    protected String buildKey(Method method, Parameter[] params, Object[] values, Map<String, Object> parMap) {
        StringBuilder keyB = new StringBuilder();

        keyB.append(method.getDeclaringClass().getName()).append(":");
        keyB.append(method.getName()).append(":");

        for (int i = 0, len = params.length; i < len; i++) {
            keyB.append(params[i].getName()).append("_").append(values[i]);
            parMap.put(params[i].getName(), values[i]);
        }

        return keyB.toString();
    }

    protected String formatTags(String tags, Map map) {
        if (tags.indexOf("$") < 0) {
            return tags;
        }

        String tags2 = tags;

        Pattern pattern = Pattern.compile("\\$\\{(\\w+)\\}");
        Matcher m = pattern.matcher(tags);
        while (m.find()) {
            String mark = m.group(0);
            String name = m.group(1);
            if (map.containsKey(name)) {
                String val = String.valueOf(map.get(name));

                tags2 = tags2.replace(mark, val);
            }
        }

        return tags2;
    }
}
