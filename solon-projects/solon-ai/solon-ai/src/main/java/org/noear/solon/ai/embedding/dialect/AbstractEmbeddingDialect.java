package org.noear.solon.ai.embedding.dialect;

import org.noear.snack.ONode;
import org.noear.solon.Utils;
import org.noear.solon.ai.embedding.*;

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
            if (Utils.isNotEmpty(config.getModel())) {
                n.set("model", config.getModel());
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
}
