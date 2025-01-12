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
package org.noear.solon.cloud.model;

import org.noear.solon.core.handle.Context;

/**
 * @author noear
 * @since 1.6
 */
public class JobImpl implements Job{
    private final JobHolder holder;
    private final Context context;

    public JobImpl(JobHolder holder, Context context) {
        this.holder = holder;
        this.context = context;
    }

    /**
     * 获取任务
     */
    public String getName() {
        return holder.getName();
    }

    /**
     * 获取计划表达式
     */
    public String getCron7x() {
        return holder.getCron7x();
    }

    /**
     * 获取描述
     */
    public String getDescription() {
        return holder.getDescription();
    }

    /**
     * 获取上下文
     */
    public Context getContext() {
        return context;
    }
}
