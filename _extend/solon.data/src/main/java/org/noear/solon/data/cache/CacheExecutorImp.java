package org.noear.solon.data.cache;

import org.noear.solon.Utils;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.data.annotation.Cache;
import org.noear.solon.data.annotation.CachePut;
import org.noear.solon.data.annotation.CacheRemove;
import org.noear.solon.data.util.InvKeys;
import org.noear.solon.ext.SupplierEx;

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
     * @param inv      拦截动作
     * @param executor 真实执行者
     */
    public Object cache(Cache anno, Invocation inv, SupplierEx executor) throws Throwable {
        if (anno == null) {
            return executor.get();
        }

        //0.构建缓存key（如果有注解的key，优先用）
        String key = anno.key();
        if (Utils.isEmpty(key)) {
            //没有注解key，生成一个key
            key = InvKeys.buildByInv(inv);
        } else {
            //格式化key
            key = InvKeys.buildByTmlAndInv(key, inv);
        }


        Object result = null;
        CacheService cs = CacheLib.cacheServiceGet(anno.service());

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
                        String tags = InvKeys.buildByTmlAndInv(anno.tags(), inv, result);
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
     * @param anno     注解
     * @param inv      拦截动作
     * @param rstValue 结果值
     */
    public void cacheRemove(CacheRemove anno, Invocation inv, Object rstValue) {
        if (anno == null) {
            return;
        }

        CacheService cs = CacheLib.cacheServiceGet(anno.service());

        //按 key 清除缓存
        if (Utils.isNotEmpty(anno.keys())) {
            String keys = InvKeys.buildByTmlAndInv(anno.keys(), inv, rstValue);

            for (String key : keys.split(",")) {
                cs.remove(key);
            }
        }

        //按 tags 清除缓存
        if (Utils.isNotEmpty(anno.tags())) {
            String tags = InvKeys.buildByTmlAndInv(anno.tags(), inv, rstValue);
            CacheTags ct = new CacheTags(cs);

            for (String tag : tags.split(",")) {
                ct.remove(tag);
            }
        }
    }

    /**
     * 缓存更新
     *
     * @param anno     注解
     * @param inv      拦截动作
     * @param rstValue 结果值（将做更新值用）
     */
    public void cachePut(CachePut anno, Invocation inv, Object rstValue) {
        if (anno == null) {
            return;
        }

        CacheService cs = CacheLib.cacheServiceGet(anno.service());

        //按 key 更新缓存
        if (Utils.isNotEmpty(anno.key())) {
            String key = InvKeys.buildByTmlAndInv(anno.key(), inv, rstValue);
            cs.store(key, rstValue, anno.seconds());
        }

        //按 tags 更新缓存
        if (Utils.isNotEmpty(anno.tags())) {
            String tags = InvKeys.buildByTmlAndInv(anno.tags(), inv, rstValue);
            CacheTags ct = new CacheTags(cs);

            for (String tag : tags.split(",")) {
                ct.update(tag, rstValue, anno.seconds());
            }
        }
    }
}