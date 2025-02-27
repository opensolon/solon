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

import org.noear.solon.ai.rag.Document;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 相似度工具
 *
 * @author noear
 * @since 3.1
 */
public final class SimilarityUtil {

    /**
     * 过滤（已经有评分的）
     */
    public static List<Document> filter(QueryCondition condition, Stream<Document> docs) throws IOException {
        return docs.filter(condition.getFilter())
                .filter(doc -> similarityCheck(doc, condition))
                .sorted(Comparator.comparing(Document::getScore).reversed())
                .limit(condition.getLimit())
                .collect(Collectors.toList());
    }

    /**
     * 评分并过滤
     */
    public static List<Document> scoreAndfilter(QueryCondition condition, Stream<Document> docs, float[] queryEmbed) throws IOException {
        return docs.filter(condition.getFilter())
                .map(doc -> copyAndScore(doc, queryEmbed))
                .filter(doc -> similarityCheck(doc, condition))
                .sorted(Comparator.comparing(Document::getScore).reversed())
                .limit(condition.getLimit())
                .collect(Collectors.toList());
    }

    /**
     * 复制并评分
     */
    public static Document copyAndScore(Document doc, float[] queryEmbed) {
        //方便调试
        return new Document(doc.getId(),
                doc.getContent(),
                doc.getMetadata(),
                cosineSimilarity(queryEmbed, doc.getEmbedding()));
    }

    /**
     * 相似度检测
     */
    private static boolean similarityCheck(Document doc, QueryCondition condition) {
        //方便调试
        return doc.getScore() >= condition.getSimilarityThreshold();
    }


    /// //////////////////////////

    /**
     * 余弦相似度
     *
     * @param embedA 嵌入矢量1
     * @param embedB 嵌入矢量2
     */
    private static double cosineSimilarity(float[] embedA, float[] embedB) {
        if (embedA != null && embedB != null) {
            if (embedA.length != embedB.length) {
                throw new IllegalArgumentException("Embed length must be equal");
            } else {
                float dotProduct = dotProduct(embedA, embedB);
                float normA = norm(embedA);
                float normB = norm(embedB);
                if (normA != 0.0F && normB != 0.0F) {
                    return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
                } else {
                    throw new IllegalArgumentException("Embed cannot be zero norm");
                }
            }
        } else {
            throw new RuntimeException("Embed must not be null");
        }
    }

    /**
     * 点积
     */
    private static float dotProduct(float[] embedA, float[] embedB) {
        if (embedA.length != embedB.length) {
            throw new IllegalArgumentException("Embed length must be equal");
        } else {
            float tmp = 0.0F;

            for (int i = 0; i < embedA.length; ++i) {
                tmp += embedA[i] * embedB[i];
            }

            return tmp;
        }
    }

    /**
     * 范数
     */
    private static float norm(float[] vector) {
        return dotProduct(vector, vector);
    }
}