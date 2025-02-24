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

import org.noear.solon.ai.rag.util.QueryCondition;
import org.noear.solon.lang.Preview;

import java.io.IOException;
import java.util.List;

/**
 * 知识库（可检索）
 *
 * @author noear
 * @since 3.1
 */
@Preview("3.1")
public interface Repository {
    /**
     * 检索
     *
     * @param query 查询字符串
     */
    default List<Document> search(String query) throws IOException {
        return search(new QueryCondition(query));
    }

    /**
     * 检索
     *
     * @param condition 查询条件
     */
    List<Document> search(QueryCondition condition) throws IOException;
}