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
package org.noear.solon.ai;

import org.noear.solon.lang.Preview;

/**
 * Ai 使用情况
 *
 * @author noear
 * @since 3.1
 */
@Preview("3.1")
public class AiUsage {
    private final long promptTokens;
    private final long completionTokens;
    private final long totalTokens;

    public AiUsage(long promptTokens, long completionTokens, long totalTokens) {
        this.promptTokens = promptTokens;
        this.completionTokens = completionTokens;
        this.totalTokens = totalTokens;
    }

    /**
     * 获取提示语消耗令牌数
     */
    public long promptTokens() {
        return promptTokens;
    }

    /**
     * 获取完成消耗令牌数
     */
    public long completionTokens() {
        return completionTokens;
    }

    /**
     * 获取总消耗令牌数
     */
    public long totalTokens() {
        return totalTokens;
    }
}