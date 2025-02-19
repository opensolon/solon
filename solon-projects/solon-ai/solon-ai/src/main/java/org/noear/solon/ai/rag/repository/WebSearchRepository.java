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
package org.noear.solon.ai.rag.repository;

import org.noear.solon.ai.AiConfig;
import org.noear.solon.ai.rag.Document;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * 网页搜索知识库（基于搜索接口实现）
 *
 * @author noear
 * @since 3.1
 */
public class WebSearchRepository implements Repository {
    private final AiConfig config;

    public WebSearchRepository() {
        this.config = new AiConfig();
        this.config.setApiUrl("https://api.bochaai.com/v1/web-search");
    }

    public WebSearchRepository(AiConfig config) {
        this.config = config;
    }

    @Override
    public List<Document> search(SearchCondition condition) throws IOException {
        return Collections.emptyList();
    }
}
