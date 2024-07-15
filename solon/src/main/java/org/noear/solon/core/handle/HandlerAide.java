/*
 * Copyright 2017-2024 noear.org and authors
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 处理助手，提供前置与后置处理的存储
 *
 * @see Gateway
 * @see Action
 * @author noear
 * @since 1.0
 * */
public class HandlerAide {
    /**
     * 前置处理
     */
    private List<Handler> befores = new ArrayList<>();
    /**
     * 后置处理
     */
    private List<Handler> afters = new ArrayList<>();

    /**
     * 添加前置处理
     */
    public void before(Handler handler) {
        befores.add(handler);
    }

    /**
     * 添加后置处理
     */
    public void after(Handler handler) {
        afters.add(handler);
    }

    /**
     * 前置处理
     */
    public Collection<Handler> befores() {
        return befores;
    }

    /**
     * 后置处理
     */
    public Collection<Handler> afters() {
        return afters;
    }
}
