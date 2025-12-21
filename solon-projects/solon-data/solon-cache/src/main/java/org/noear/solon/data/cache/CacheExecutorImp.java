/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.data.cache;

import org.noear.solon.Utils;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.core.util.SnelUtil;
import org.noear.solon.core.util.SupplierEx;
import org.noear.solon.data.annotation.Cache;
import org.noear.solon.data.annotation.CachePut;
import org.noear.solon.data.annotation.CacheRemove;
import org.noear.solon.data.util.InvKeys;
import org.noear.solon.data.util.StringMutexLock;

import java.lang.reflect.Type;

/**
 * 缓存执行器
 *
 * @author noear
 * @since 1.0
 * */
public class CacheExecutorImp {
    public static final CacheExecutorImp global = new CacheExecutorImp();
    private final StringMutexLock LOCK = new StringMutexLock();

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
            key = SnelUtil.evalTmpl(key, inv);
        }

        CacheService cs = CacheLib.cacheServiceGet(anno.service());

        //1.从缓存获取
        //
        Type type = inv.method().getGenericReturnType();
        if (type == null) {
            type = inv.method().getReturnType();
        }

        //第一次尝试（无锁读）: 绝大多数请求在此返回
        Object result = cs.get(key, type);
        if (result != null) {
            return result;
        }

        try (AutoCloseable entry = LOCK.lockEntry(key)) {
            //第二次尝试（双重检查）:
            result = cs.get(key, type);

            if (result == null) {
                //真正执行业务逻辑
                result = executor.get();

                if (result != null) {
                    cs.store(key, result, anno.seconds());

                    if (Utils.isNotEmpty(anno.tags())) {
                        String tags = SnelUtil.evalTmpl(anno.tags(), inv);
                        CacheTags ct = new CacheTags(cs);

                        //4.添加缓存标签
                        for (String tag : tags.split(",")) {
                            ct.add(tag, key, anno.seconds());
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
            String keys = SnelUtil.evalTmpl(anno.keys(), inv);

            for (String key : keys.split(",")) {
                cs.remove(key);
            }
        }

        //按 tags 清除缓存
        if (Utils.isNotEmpty(anno.tags())) {
            String tags = SnelUtil.evalTmpl(anno.tags(), inv);
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
            String key = SnelUtil.evalTmpl(anno.key(), inv);
            cs.store(key, rstValue, anno.seconds());
        }

        //按 tags 更新缓存
        if (Utils.isNotEmpty(anno.tags())) {
            String tags = SnelUtil.evalTmpl(anno.tags(), inv);
            CacheTags ct = new CacheTags(cs);

            for (String tag : tags.split(",")) {
                ct.update(tag, rstValue, anno.seconds());
            }
        }
    }
}