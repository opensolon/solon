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

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 文档加载器
 *
 * @author noear
 * @since 3.1
 */
@Preview("3.1")
public interface DocumentLoader {
    /**
     * 附加元数据
     */
    DocumentLoader additionalMetadata(String key, Object value);

    /**
     * 附加元数据
     */
    DocumentLoader additionalMetadata(Map<String, Object> metadata);

    /**
     * 加载文档
     */
    List<Document> load() throws IOException;
}
