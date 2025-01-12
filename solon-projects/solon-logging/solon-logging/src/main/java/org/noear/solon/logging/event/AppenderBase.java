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
package org.noear.solon.logging.event;

/**
 * 日志添加器基类
 *
 * @author noear
 * @since 1.3
 */
public abstract class AppenderBase implements Appender{
    /**
     * 名称
     * */
    private String name;

    /**
     * 获取名称
     * */
    @Override
    public String getName() {
        return name;
    }

    /**
     * 设置名称
     *
     * @param name 名称
     * */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 开始生命周期（一般由 AppenderHolder 控制 ）
     *
     * @see org.noear.solon.logging.AppenderHolder
     * */
    @Override
    public void start() {

    }

    /**
     * 停止生命周期
     * */
    @Override
    public void stop() {

    }
}
