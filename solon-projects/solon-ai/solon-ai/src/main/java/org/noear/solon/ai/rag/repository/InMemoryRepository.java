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

import org.noear.solon.ai.rag.EmbeddingModel;
import org.noear.solon.ai.rag.State;
import org.noear.solon.ai.rag.loader.DocumentLoader;
import org.noear.solon.lang.Preview;

/**
 * @author noear
 * @since 3.1
 */
@Preview("3.1")
public class InMemoryRepository implements Repository {

    public InMemoryRepository(EmbeddingModel embeddings) {

    }

    @Override
    public void load(DocumentLoader loader) {

    }

    @Override
    public State retrieve(String question) {
        return null;
    }
}
