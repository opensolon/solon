package org.noear.solon.ai.embedding.dialect;

import org.noear.snack.ONode;
import org.noear.solon.ai.AiUsage;
import org.noear.solon.ai.embedding.Embedding;
import org.noear.solon.ai.embedding.EmbeddingConfig;
import org.noear.solon.ai.embedding.EmbeddingResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * @author noear
 * @since 3.1
 */
public class OllamaEmbeddingDialect extends AbstractEmbeddingDialect {
    private static OllamaEmbeddingDialect instance = new OllamaEmbeddingDialect();

    public static OllamaEmbeddingDialect getInstance() {
        return instance;
    }

    @Override
    public boolean matched(EmbeddingConfig config) {
        return "ollama".equals(config.provider());
    }

    @Override
    public EmbeddingResponse parseResponseJson(EmbeddingConfig config, String respJson) {
        ONode oResp = ONode.load(respJson);

        String model = oResp.get("model").getString();

        List<Embedding> data = new ArrayList<>();
        int dataIndex = 0;
        for (float[] embed : oResp.get("embeddings").toObjectList(float[].class)) {
            data.add(new Embedding(dataIndex, embed));
            dataIndex++;
        }

        AiUsage usage = null;

        if (oResp.contains("prompt_eval_count")) {
            int prompt_eval_count = oResp.get("prompt_eval_count").getInt();
            usage = new AiUsage(
                    prompt_eval_count,
                    0,
                    prompt_eval_count
            );
        }

        return new EmbeddingResponse(model, data, usage);
    }
}
