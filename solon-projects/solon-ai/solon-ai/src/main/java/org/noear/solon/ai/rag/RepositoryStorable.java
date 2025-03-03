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

/**
 * 可存储的知识库（可存储）
 *
 * @author noear
 * @since 3.1
 */
@Preview("3.1")
public interface RepositoryStorable extends Repository {
    /**
     * 插入
     */
    void insert(List<Document> documents) throws IOException;

    /**
     * 删除
     */
    void delete(String... ids) throws IOException;

    /**
     * 是否存在
     */
    boolean exists(String id) throws IOException;
}