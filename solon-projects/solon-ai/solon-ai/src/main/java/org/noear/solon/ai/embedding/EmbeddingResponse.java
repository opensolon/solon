package org.noear.solon.ai.embedding;

import java.util.List;

/**
 * 嵌入响应
 *
 * @author noear
 * @since 3.1
 */
public class EmbeddingResponse {
    private final String model;
    private final List<EmbeddingCheuk> data;
    private final EmbeddingUsage usage;

    public EmbeddingResponse(String model, List<EmbeddingCheuk> data, EmbeddingUsage usage) {
        this.model = model;
        this.data = data;
        this.usage = usage;
    }

    public String getModel() {
        return model;
    }

    public List<EmbeddingCheuk> getData() {
        return data;
    }

    public EmbeddingUsage getUsage() {
        return usage;
    }

    @Override
    public String toString() {
        return "{" +
                "model='" + model + '\'' +
                ", data=" + data +
                ", usage=" + usage +
                '}';
    }
}
