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
package org.noear.solon.ai.embedding.dialect;

import org.noear.snack.ONode;
import org.noear.solon.ai.AiUsage;
import org.noear.solon.ai.embedding.Embedding;
import org.noear.solon.ai.embedding.EmbeddingConfig;
import org.noear.solon.ai.embedding.EmbeddingException;
import org.noear.solon.ai.embedding.EmbeddingResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Ollama 内嵌模型方言
 *
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
        return "ollama".equals(config.getProvider());
    }

    @Override
    public EmbeddingResponse parseResponseJson(EmbeddingConfig config, String respJson) {
        ONode oResp = ONode.load(respJson);

        String model = oResp.get("model").getString();

        if (oResp.contains("error")) {
            return new EmbeddingResponse(model, new EmbeddingException(oResp.get("error").getString()), null, null);
        } else {
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

            return new EmbeddingResponse(model, null, data, usage);
        }
    }
}
