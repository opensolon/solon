package org.noear.solon.ai.embedding.dialect;

import org.noear.snack.ONode;
import org.noear.solon.Utils;
import org.noear.solon.ai.embedding.*;

import java.util.ArrayList;
import java.util.List;

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
        }).toJson();
    }

    @Override
    public EmbeddingResponse parseResponseJson(EmbeddingConfig config, String json) {
        ONode oResp = ONode.load(json);

        String model = oResp.get("model").getString();
        List<EmbeddingCheuk> data = new ArrayList<>();
        EmbeddingUsage usage = null;

        for (ONode m1 : oResp.get("data").ary()) {
            //不使用反序列化，可方便别的阅读和修改定制
            data.add(new EmbeddingCheuk(
                    m1.get("index").getInt(),
                    m1.get("embedding").toObjectList(Float.class)));
        }

        if (oResp.contains("usage")) {
            ONode oUsage = oResp.get("usage");
            usage = new EmbeddingUsage(
                    oUsage.get("prompt_tokens").getInt(),
                    0,
                    oUsage.get("total_tokens").getInt()
            );
        }


        return new EmbeddingResponse(model, data, usage);
    }
}
