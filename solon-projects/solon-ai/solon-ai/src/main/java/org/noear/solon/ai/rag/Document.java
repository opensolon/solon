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
package org.noear.solon.ai.rag;

import lombok.Builder;
import lombok.Getter;
import org.noear.solon.lang.Preview;

/**
 * 文档
 *
 * @author noear
 * @since 3.1
 */
@Builder
@Getter
@Preview("3.1")
public class Document {
    /**
     * id
     */
    private String id;

    /**
     * 标题
     */
    private String title;

    /**
     * 网址
     */
    private String url;

    /**
     * 片段
     */
    private String snippet;

    /**
     * 摘要
     */
    private String summary;

    /**
     * 语言
     */
    private String language;
}