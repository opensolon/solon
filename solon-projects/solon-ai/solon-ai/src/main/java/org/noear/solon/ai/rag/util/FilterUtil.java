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
package org.noear.solon.ai.rag.util;

import org.noear.solon.ai.embedding.EmbeddingModel;
import org.noear.solon.ai.rag.Document;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 过滤工具
 *
 * @author noear
 * @since 3.1
 */
public final class FilterUtil {

    public static List<Document> filter(QueryCondition condition, Stream<Document> docs) throws IOException {
        return docs.filter(condition.getFilter())
                .filter(doc -> filterDo(doc, condition))
                .sorted(Comparator.comparing(Document::getScore).reversed())
                .limit(condition.getLimit())
                .collect(Collectors.toList());
    }

    public static List<Document> similarityFilter(QueryCondition condition, EmbeddingModel embeddingModel, Stream<Document> docs) throws IOException {
        float[] queryEmbed = embeddingModel.embed(condition.getQuery());

        return similarityFilter(condition, docs, queryEmbed);
    }

    /**
     * 相似度过滤
     */
    public static List<Document> similarityFilter(QueryCondition condition, Stream<Document> docs, float[] queryEmbed) throws IOException {
        return docs.filter(condition.getFilter())
                .map(doc -> mapDo(doc, queryEmbed))
                .filter(doc -> filterDo(doc, condition))
                .sorted(Comparator.comparing(Document::getScore).reversed())
                .limit(condition.getLimit())
                .collect(Collectors.toList());
    }

    private static Document mapDo(Document doc, float[] queryEmbed) {
        //方便调试
        return new Document(doc.getId(),
                doc.getContent(),
                doc.getMetadata(),
                SimilarityMath.cosineSimilarity(queryEmbed, doc.getEmbedding()));
    }

    private static boolean filterDo(Document doc, QueryCondition condition) {
        //方便调试
        return doc.getScore() >= condition.getSimilarityThreshold();
    }
}