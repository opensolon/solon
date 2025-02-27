package org.noear.solon.ai.image.dialect;

import org.noear.snack.ONode;
import org.noear.solon.ai.AiUsage;
import org.noear.solon.ai.image.Embedding;
import org.noear.solon.ai.image.ImageConfig;
import org.noear.solon.ai.image.ImageResponse;

import java.util.List;

/**
 * @author noear
 * @since 3.1
 */
public class OpenaiImageDialect extends AbstractImageDialect {
    private static OpenaiImageDialect instance = new OpenaiImageDialect();

    public static OpenaiImageDialect getInstance() {
        return instance;
    }

    @Override
    public boolean matched(ImageConfig config) {
        return false;
    }

    @Override
    public ImageResponse parseResponseJson(ImageConfig config, String respJson) {
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

        return new ImageResponse(model, data, usage);
    }
}
