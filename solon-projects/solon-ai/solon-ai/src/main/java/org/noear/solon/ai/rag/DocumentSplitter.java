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

import org.noear.solon.lang.Preview;

import java.util.Arrays;
import java.util.List;

/**
 * 文档分割器
 *
 * @author noear
 * @since 3.1
 */
@Preview("3.1")
public interface DocumentSplitter {
    /**
     * 分割
     *
     * @param text 文本
     */
    default List<Document> split(String text) {
        return split(Arrays.asList(new Document(text)));
    }

    /**
     * 分割
     *
     * @param documents 文档
     */
    List<Document> split(List<Document> documents);
}
