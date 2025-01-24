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
package org.noear.solon.flow;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 记数器
 *
 * @author noear
 * @since 3.0
 */
public class Counter {
    private final Map<String, AtomicInteger> counts = new LinkedHashMap<>();
    private final Map<String, Stack<Integer>> stacks = new LinkedHashMap<>();

    /**
     * 记录栈
     */
    protected Stack<Integer> stack(Chain chain, String key) {
        return stacks.computeIfAbsent(chain.id() + "/" + key, k -> new Stack<>());
    }


    /**
     * 获取
     */
    protected int get(Chain chain, String key) {
        return counts.computeIfAbsent(chain.id() + "/" + key, k -> new AtomicInteger(0))
                .get();
    }

    /**
     * 设置
     */
    protected void set(Chain chain, String key, int value) {
        counts.computeIfAbsent(chain.id() + "/" + key, k -> new AtomicInteger(0))
                .set(value);
    }

    /**
     * 增量
     */
    protected int incr(Chain chain, String key) {
        return counts.computeIfAbsent(chain.id() + "/" + key, k -> new AtomicInteger(0))
                .incrementAndGet();
    }

    @Override
    public String toString() {
        return "{" +
                "counts=" + counts +
                ", stacks=" + stacks +
                '}';
    }
}
