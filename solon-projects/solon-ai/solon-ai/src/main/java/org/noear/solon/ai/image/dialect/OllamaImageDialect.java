package org.noear.solon.ai.image.dialect;

import org.noear.snack.ONode;
import org.noear.solon.ai.AiUsage;
import org.noear.solon.ai.image.Embedding;
import org.noear.solon.ai.image.ImageConfig;
import org.noear.solon.ai.image.ImageResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * @author noear
 * @since 3.1
 */
public class OllamaImageDialect extends AbstractImageDialect {
    private static OllamaImageDialect instance = new OllamaImageDialect();

    public static OllamaImageDialect getInstance() {
        return instance;
    }

    @Override
    public boolean matched(ImageConfig config) {
        return "ollama".equals(config.getProvider());
    }

    @Override
    public ImageResponse parseResponseJson(ImageConfig config, String respJson) {
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

        return new ImageResponse(model, data, usage);
    }
}
