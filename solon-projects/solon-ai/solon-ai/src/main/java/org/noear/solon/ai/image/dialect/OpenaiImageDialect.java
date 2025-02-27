/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.ai.image.dialect;

import org.noear.snack.ONode;
import org.noear.solon.ai.AiUsage;
import org.noear.solon.ai.embedding.EmbeddingException;
import org.noear.solon.ai.embedding.EmbeddingResponse;
import org.noear.solon.ai.image.Image;
import org.noear.solon.ai.image.ImageConfig;
import org.noear.solon.ai.image.ImageException;
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

        if (oResp.contains("error")) {
            return new ImageResponse(model, new ImageException(oResp.get("error").getString()), null, null);
        } else {
            List<Image> data = oResp.get("data").toObjectList(Image.class);

            AiUsage usage = null;
            if (oResp.contains("usage")) {
                ONode oUsage = oResp.get("usage");
                usage = new AiUsage(
                        oUsage.get("prompt_tokens").getInt(),
                        0,
                        oUsage.get("total_tokens").getInt()
                );
            }

            return new ImageResponse(model, null, data, usage);
        }
    }
}
