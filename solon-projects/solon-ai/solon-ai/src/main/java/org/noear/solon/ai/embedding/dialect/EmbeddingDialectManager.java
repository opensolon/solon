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
package org.noear.solon.ai.embedding.dialect;

import org.noear.solon.ai.embedding.EmbeddingConfig;
import org.noear.solon.core.util.RankEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 内嵌模型方言管理
 *
 * @author noear
 */
public class EmbeddingDialectManager {
    private static List<RankEntity<EmbeddingDialect>> dialects = new ArrayList<>();

    static {
        register(OllamaEmbeddingDialect.getInstance());
        register(DashscopeEmbeddingDialect.getInstance());
    }

    /**
     * 选择方言
     */
    public static EmbeddingDialect select(EmbeddingConfig config) {
        for (RankEntity<EmbeddingDialect> d : dialects) {
            if (d.target.matched(config)) {
                return d.target;
            }
        }

        return OpenaiEmbeddingDialect.getInstance();
    }

    /**
     * 注册方言
     */
    public static void register(EmbeddingDialect dialect) {
        register(dialect, 0);
    }

    /**
     * 注册方言
     */
    public static void register(EmbeddingDialect dialect, int index) {
        dialects.add(new RankEntity<>(dialect, index));
        Collections.sort(dialects);
    }

    /**
     * 注销方言
     */
    public static void unregister(EmbeddingDialect dialect) {
        dialects.removeIf(rankEntity -> rankEntity.target == dialect);
    }
}