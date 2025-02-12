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
package org.noear.solon.ai.chat;

/**
 * 聊天响应修改器
 *
 * @author noear
 * @since 3.1
 */
public class ChatResponseAmend {
    private final ChatResponseDefault real;

    public ChatResponseAmend(ChatResponseDefault real) {
        this.real = real;
    }

    /**
     * 思考中
     * */
    public boolean reasoning;

    /**
     * 获取真实聊天响应
     */
    public ChatResponse getReal() {
        return real;
    }

    /**
     * 重置响应数据
     */
    public void reset() {
        real.error = null;
        real.choices.clear();
    }

    /**
     * 添加选择
     */
    public void addChoice(ChatChoice choice) {
        real.choices.add(choice);
    }

    /**
     * 设置错误
     */
    public void setError(ChatException error) {
        real.error = error;
    }

    /**
     * 设置使用情况
     */
    public void setUsage(ChatUsage usage) {
        real.usage = usage;
    }

    /**
     * 设置模型
     */
    public void setModel(String model) {
        real.model = model;
    }

    /**
     * 设置完成状态
     */
    public void setFinished(boolean finished) {
        real.finished = finished;
    }

    /**
     * 是否已完成
     */
    public boolean isFinished() {
        return real.finished;
    }
}