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
package org.noear.solon.rx.handle;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.util.RankEntity;
import org.noear.solon.rx.Completable;

import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 响应式请求链管理
 *
 * @author noear
 * @since 3.1
 */
public class RxChainManager<CTX> {
    /**
     * 类型集合（用于重复检测）
     */
    private final Set<Class<?>> typeSet = new HashSet<>();

    /**
     * 过滤器 节点
     */
    private final List<RankEntity<RxFilter<CTX>>> filterNodes = new ArrayList<>();

    private final ReentrantLock SYNC_LOCK = new ReentrantLock();

    public Collection<RxFilter<CTX>> getFilterNodes() {
        List<RxFilter<CTX>> tmp = new ArrayList<>();

        for (RankEntity<RxFilter<CTX>> entity : filterNodes) {
            tmp.add(entity.target);
        }

        return tmp;
    }

    /**
     * 添加过滤器
     */
    public void addFilter(RxFilter<CTX> filter, int index) {
        SYNC_LOCK.lock();

        try {
            typeSet.add(filter.getClass());

            filterNodes.add(new RankEntity(filter, index));
            Collections.sort(filterNodes);
        } finally {
            SYNC_LOCK.unlock();
        }
    }

    /**
     * 添加过滤器，如果有相同类的则不加
     */
    public void addFilterIfAbsent(RxFilter<CTX> filter, int index) {
        SYNC_LOCK.lock();

        try {
            if (typeSet.contains(filter.getClass())) {
                return;
            }

            //有同步锁，就不复用上面的代码了
            typeSet.add(filter.getClass());

            filterNodes.add(new RankEntity(filter, index));
            filterNodes.sort(Comparator.comparingInt(f -> f.index));
        } finally {
            SYNC_LOCK.unlock();
        }
    }

    /**
     * 执行过滤
     */
    public Completable doFilter(CTX x, RxHandler<CTX> lastHandler) throws Throwable {
        return new RxFilterChainImpl(filterNodes, lastHandler).doFilter(x);
    }
}
