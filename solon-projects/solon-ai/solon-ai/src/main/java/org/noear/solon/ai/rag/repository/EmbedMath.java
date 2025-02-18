package org.noear.solon.ai.rag.repository;

public interface EmbedMath {
    /**
     * 余弦相似度
     */
    static double cosineSimilarity(float[] embedA, float[] embedB) {
        if (embedA != null && embedB != null) {
            if (embedA.length != embedB.length) {
                throw new IllegalArgumentException("Embed lengths must be equal");
            } else {
                float dotProduct = dotProduct(embedA, embedB);
                float normX = norm(embedA);
                float normY = norm(embedB);
                if (normX != 0.0F && normY != 0.0F) {
                    return (double) dotProduct / (Math.sqrt((double) normX) * Math.sqrt((double) normY));
                } else {
                    throw new IllegalArgumentException("Embed cannot have zero norm");
                }
            }
        } else {
            throw new RuntimeException("Embed must not be null");
        }
    }

    /**
     * 点积
     */
    static float dotProduct(float[] embedA, float[] embedB) {
        if (embedA.length != embedB.length) {
            throw new IllegalArgumentException("Embed lengths must be equal");
        } else {
            float result = 0.0F;

            for (int i = 0; i < embedA.length; ++i) {
                result += embedA[i] * embedB[i];
            }

            return result;
        }
    }

    /**
     * 范数
     */
    static float norm(float[] vector) {
        return dotProduct(vector, vector);
    }
}
