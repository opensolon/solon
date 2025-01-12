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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 处理助手，提供前置与后置处理的存储
 *
 * @see Gateway
 * @see Action
 * @author noear
 * @since 1.0
 * @since 3.0
 * */
public class HandlerAide {
    /**
     * 过滤列表
     */
    private List<RankEntity<Filter>> filters = new ArrayList<>();

    /**
     * 添加过滤器（按先进后出策略执行）
     *
     * @param filter 过滤器
     * @since 2.9
     */
    public void filter(Filter filter) {
        filter(0, filter);
    }

    /**
     * 添加过滤器（按先进后出策略执行）
     *
     * @param filter 过滤器
     * @param index  顺序
     * @since 2.9
     */
    public void filter(int index, Filter filter) {
        filters.add(new RankEntity<>(filter, index));
        filters.sort(Comparator.comparingInt(f -> f.index));
    }

    /**
     * 过滤列表
     *
     * @since 2.9
     */
    public List<RankEntity<Filter>> filters() {
        return filters;
    }
}
