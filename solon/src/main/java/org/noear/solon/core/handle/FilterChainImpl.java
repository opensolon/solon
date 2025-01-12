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
package org.noear.solon.core.handle;

import org.noear.solon.core.util.RankEntity;

import java.util.List;

/**
 * 过滤器调用链实现
 *
 * @author noear
 * @since 1.3
 * */
public class FilterChainImpl implements FilterChain {
    private final List<RankEntity<Filter>> filterList;
    private final Handler lastHandler;
    private int index;

    public FilterChainImpl(List<RankEntity<Filter>> filterList) {
        this(filterList, null);
    }

    public FilterChainImpl(List<RankEntity<Filter>> filterList, Handler lastHandler) {
        this.filterList = filterList;
        this.index = 0;
        this.lastHandler = lastHandler;
    }

    @Override
    public void doFilter(Context ctx) throws Throwable {
        if (lastHandler == null) {
            filterList.get(index++).target.doFilter(ctx, this);
        } else {
            if (index < filterList.size()) {
                filterList.get(index++).target.doFilter(ctx, this);
            } else {
                lastHandler.handle(ctx);
            }
        }
    }
}