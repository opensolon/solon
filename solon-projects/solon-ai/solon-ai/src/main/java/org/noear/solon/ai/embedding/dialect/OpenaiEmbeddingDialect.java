package org.noear.solon.ai.embedding.dialect;

import org.noear.snack.ONode;
import org.noear.solon.ai.AiUsage;
import org.noear.solon.ai.embedding.Embedding;
import org.noear.solon.ai.embedding.EmbeddingConfig;
import org.noear.solon.ai.embedding.EmbeddingResponse;

import java.util.List;

/**
 * @author noear
 * @since 3.1
 */
public class OpenaiEmbeddingDialect extends AbstractEmbeddingDialect {
    private static OpenaiEmbeddingDialect instance = new OpenaiEmbeddingDialect();

    public static OpenaiEmbeddingDialect getInstance() {
        return instance;
    }

    @Override
    public boolean matched(EmbeddingConfig config) {
        return false;
    }

    @Override
    public EmbeddingResponse parseResponseJson(EmbeddingConfig config, String respJson) {
        ONode oResp = ONode.load(respJson);

        String model = oResp.get("model").getString();
        List<Embedding> data = oResp.get("data").toObjectList(Embedding.class);
        AiUsage usage = null;

        if (oResp.contains("usage")) {
            ONode oUsage = oResp.get("usage");
            usage = new AiUsage(
                    oUsage.get("prompt_tokens").getInt(),
                    0,
                    oUsage.get("total_tokens").getInt()
            );
        }

        return new EmbeddingResponse(model, data, usage);
    }
}
