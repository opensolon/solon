package org.noear.solon.ai.embedding.dialect;

import org.noear.snack.ONode;
import org.noear.solon.Utils;
import org.noear.solon.ai.common.Usage;
import org.noear.solon.ai.embedding.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author noear
 * @since 3.1
 */
public abstract class AbstractEmbeddingDialect implements EmbeddingDialect {
    @Override
    public String buildRequestJson(EmbeddingConfig config, EmbeddingOptions options, List<String> input) {
        return new ONode().build(n -> {
            if (Utils.isNotEmpty(config.model())) {
                n.set("model", config.model());
            }

            n.getOrNew("input").build(n1 -> {
                for (String m1 : input) {
                    n1.add(m1);
                }
            });

            n.set("encoding_format", "float");

            for (Map.Entry<String, Object> kv : options.options().entrySet()) {
                n.set(kv.getKey(), kv.getValue());
            }
        }).toJson();
    }

    @Override
    public EmbeddingResponse parseResponseJson(EmbeddingConfig config, String respJson) {
        ONode oResp = ONode.load(respJson);

        String model = oResp.get("model").getString();
        List<Embedding> data = oResp.get("data").toObjectList(Embedding.class);
        Usage usage = null;

        if (oResp.contains("usage")) {
            ONode oUsage = oResp.get("usage");
            usage = new Usage(
                    oUsage.get("prompt_tokens").getInt(),
                    0,
                    oUsage.get("total_tokens").getInt()
            );
        }

        return new EmbeddingResponse(model, data, usage);
    }
}
