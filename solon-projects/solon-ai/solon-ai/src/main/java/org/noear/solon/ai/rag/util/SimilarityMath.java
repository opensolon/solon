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


/**
 * 相似度算法
 * */
public final class SimilarityMath {
    /**
     * 余弦相似度
     *
     * @param embedA 嵌入矢量1
     * @param embedB 嵌入矢量2
     */
    public static double cosineSimilarity(float[] embedA, float[] embedB) {
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
    public static float dotProduct(float[] embedA, float[] embedB) {
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
    public static float norm(float[] vector) {
        return dotProduct(vector, vector);
    }
}
