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
import org.noear.solon.ai.image.Image;
import org.noear.solon.ai.image.ImageConfig;
import org.noear.solon.ai.image.ImageException;
import org.noear.solon.ai.image.ImageResponse;

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

        if (oResp.contains("error")) {
            return new ImageResponse(model, new ImageException(oResp.get("error").getString()), null, null);
        } else {
            List<Image> data = oResp.get("data").toObjectList(Image.class);

            AiUsage usage = null;
            if (oResp.contains("prompt_eval_count")) {
                int prompt_eval_count = oResp.get("prompt_eval_count").getInt();
                usage = new AiUsage(
                        prompt_eval_count,
                        0,
                        prompt_eval_count
                );
            }

            return new ImageResponse(model, null, data, usage);
        }
    }
}
