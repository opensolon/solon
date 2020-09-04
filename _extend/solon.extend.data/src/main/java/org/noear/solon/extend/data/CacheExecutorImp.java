package org.noear.solon.extend.data;

import org.noear.solon.XUtil;
import org.noear.solon.annotation.XCache;
import org.noear.solon.core.CacheService;
import org.noear.solon.core.XBridge;
import org.noear.solon.core.XCacheExecutor;
import org.noear.solon.ext.SupplierEx;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 缓存执行器
 * */
public class CacheExecutorImp implements XCacheExecutor {
    @Override
    public Object execute(XCache anno, Method method, Parameter[] params, Object[] values, SupplierEx callable) throws Throwable {
        CacheService cs = XBridge.cacheServiceGet(anno.caching());

        if (XUtil.isNotEmpty(anno.tags())) {
            String tags = anno.tags();

            StringBuilder keyB = new StringBuilder();

            keyB.append(method.getDeclaringClass().getName()).append(":");
            keyB.append(method.getName()).append(":");

            for (int i = 0, len = params.length; i < len; i++) {
                keyB.append(params[i].getName()).append("_").append(values[i]);
            }

            String key = keyB.toString();

            //1.从缓存获取

            Object cacheT = cs.get(key);
            if (cacheT == null) {
                //2.执行调用，并返回
                cacheT = callable.get();
                cs.store(key, cacheT, anno.seconds());

                CacheTags ct = new CacheTags(cs);

                //添加缓存
                for(String tag : tags.split(",")) {
                    ct.add(tag, key);
                }
            }
        }

        return null;
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
