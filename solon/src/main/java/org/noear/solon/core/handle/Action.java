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

import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.wrap.MethodWrap;
import org.noear.solon.lang.Nullable;

/**
 * mvc:动作接口
 *
 * @author noear
 * @since 2.7
 * */
public interface Action extends Handler {
    /**
     * 名字
     */
    String name();

    /**
     * 全名
     */
    String fullName();

    /**
     * 映射
     */
    @Nullable
    Mapping mapping();

    /**
     * 执行方法
     */
    MethodWrap method();

    /**
     * 控制器包装
     */
    BeanWrap controller();

    /**
     * 生产格式
     */
    String produces();

    /**
     * 消费格式
     */
    String consumes();

    /**
     * 调用
     */
    void invoke(Context c, Object obj) throws Throwable;

    /*
     * 渲染
     * */
    void render(Object obj, Context c, boolean allowMultiple) throws Throwable;
}
