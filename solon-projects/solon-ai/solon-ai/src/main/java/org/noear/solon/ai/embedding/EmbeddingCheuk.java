package org.noear.solon.ai.embedding;

import java.util.List;

/**
 * @author noear 2025/2/17 created
 */
public class EmbeddingCheuk {
    private int index;
    private List<Float> embedding;

    public EmbeddingCheuk() {
        //用于反序列化
    }

    public EmbeddingCheuk(int index, List<Float> embedding) {
        this.embedding = embedding;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public List<Float> getEmbedding() {
        return embedding;
    }

    @Override
    public String toString() {
        return "{" +
                "index=" + index +
                ", embedding=" + embedding +
                '}';
    }
}
