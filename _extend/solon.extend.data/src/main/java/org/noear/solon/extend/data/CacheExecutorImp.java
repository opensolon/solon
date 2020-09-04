package org.noear.solon.extend.data;

import org.noear.solon.XUtil;
import org.noear.solon.annotation.XCache;
import org.noear.solon.core.CacheService;
import org.noear.solon.core.XBridge;
import org.noear.solon.core.XCacheExecutor;
import org.noear.solon.ext.SupplierEx;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 缓存执行器
 * */
public class CacheExecutorImp implements XCacheExecutor {
    public static final CacheExecutorImp global = new CacheExecutorImp();


    @Override
    public Object execute(XCache anno, Method method, Parameter[] params, Object[] values, SupplierEx callable) throws Throwable {
        CacheService cs = XBridge.cacheServiceGet(anno.caching());
        Map<String,Object> parMap = new HashMap<>();
        Object cacheT = null;

        //清除缓存标签
        //
        if (anno.seconds() != 0 || XUtil.isNotEmpty(anno.tags())) {
            StringBuilder keyB = new StringBuilder();

            keyB.append(method.getDeclaringClass().getName()).append(":");
            keyB.append(method.getName()).append(":");

            for (int i = 0, len = params.length; i < len; i++) {
                keyB.append(params[i].getName()).append("_").append(values[i]);
                parMap.put(params[i].getName(), values[i]);
            }

            String key = keyB.toString();

            //1.从缓存获取

            cacheT = cs.get(key);
            if (cacheT == null) {
                //2.执行调用，并返回
                cacheT = callable.get();
                cs.store(key, cacheT, anno.seconds());


                if(XUtil.isNotEmpty(anno.tags())) {
                    String tags = formatTags(anno.tags(), parMap);
                    CacheTags ct = new CacheTags(cs);

                    //添加缓存
                    for (String tag : tags.split(",")) {
                        ct.add(tag, key);
                    }
                }
            }
        }else{
            cacheT = callable.get();
        }

        //清除缓存标签
        //
        if (XUtil.isNotEmpty(anno.clear())) {
            if(parMap.size() == 0) {
                for (int i = 0, len = params.length; i < len; i++) {
                    parMap.put(params[i].getName(), values[i]);
                }
            }

            String tags = formatTags(anno.clear(),parMap);
            CacheTags ct = new CacheTags(cs);

            //添加缓存
            for(String tag : tags.split(",")) {
                ct.clear(tag);
            }
        }

        return cacheT;
    }

    private String formatTags(String tags, Map map) {
        String tags2 = tags;

        Pattern pattern = Pattern.compile("\\$\\{(\\w+)\\}");
        Matcher m = pattern.matcher(tags);
        while (m.find()) {
            String mark = m.group(0);
            String name = m.group(1);
            if(map.containsKey(name)){
                String val = String.valueOf(map.get(name));

                tags2 = tags2.replace(mark, val);
            }
        }

        return tags2;
    }
}
