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
package org.noear.solon.cloud.gateway.exchange;

import org.noear.solon.lang.Nullable;
import org.noear.solon.rx.Baba;
import org.noear.solon.core.util.RankEntity;

import java.util.List;

/**
 * 交换过滤器链实现
 *
 * @author noear
 * @since 2.9
 */
public class ExFilterChainImpl implements ExFilterChain {
    private final List<RankEntity<ExFilter>> filterList;
    private final ExHandler lastHandler;
    private int index;

    public ExFilterChainImpl() {
        //一般用于测试
        this(null, null);
    }

    public ExFilterChainImpl(@Nullable List<RankEntity<ExFilter>> filterList) {
        this(filterList, null);
    }

    public ExFilterChainImpl(List<RankEntity<ExFilter>> filterList, ExHandler lastHandler) {
        this.filterList = filterList;
        this.index = 0;
        this.lastHandler = lastHandler;
    }

    /**
     * 过滤
     *
     * @param ctx 交换上下文
     */
    @Override
    public Baba<Void> doFilter(ExContext ctx) {
        if (filterList == null) {
            //一般用于测试
            return Baba.complete();
        }

        if (lastHandler == null) {
            return filterList.get(index++).target.doFilter(ctx, this);
        } else {
            if (index < filterList.size()) {
                return filterList.get(index++).target.doFilter(ctx, this);
            } else {
                return lastHandler.handle(ctx);
            }
        }
    }
}