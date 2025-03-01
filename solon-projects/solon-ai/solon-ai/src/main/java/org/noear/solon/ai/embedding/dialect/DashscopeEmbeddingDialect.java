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
import org.noear.solon.Utils;
import org.noear.solon.ai.AiUsage;
import org.noear.solon.ai.embedding.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * DashScope 嵌入模型方言（阿里云产品）
 *
 * @author noear
 * @since 3.1
 */

public class DashscopeEmbeddingDialect extends AbstractEmbeddingDialect {
    //https://help.aliyun.com/zh/model-studio/developer-reference

    private static DashscopeEmbeddingDialect instance = new DashscopeEmbeddingDialect();

    public static DashscopeEmbeddingDialect getInstance() {
        return instance;
    }

    /**
     * 匹配检测
     *
     * @param config 聊天配置
     */
    @Override
    public boolean matched(EmbeddingConfig config) {
        return "dashscope".equals(config.getProvider());

    }

    @Override
    public String buildRequestJson(EmbeddingConfig config, EmbeddingOptions options, List<String> input) {
        return new ONode().build(n -> {
            if (Utils.isNotEmpty(config.getModel())) {
                n.set("model", config.getModel());
            }

            n.getOrNew("input").getOrNew("texts").build(n1 -> {
                for (String m1 : input) {
                    n1.add(m1);
                }
            });

            n.getOrNew("parameters").build(n1 -> {
                for (Map.Entry<String, Object> kv : options.options().entrySet()) {
                    n1.set(kv.getKey(), kv.getValue());
                }
            });


        }).toJson();
    }

    @Override
    public EmbeddingResponse parseResponseJson(EmbeddingConfig config, String respJson) {
        ONode oResp = ONode.load(respJson);

        String model = oResp.get("model").getString();

        if (oResp.contains("error")) {
            return new EmbeddingResponse(model, new EmbeddingException(oResp.get("error").getString()), null, null);
        } else {
            List<Embedding> data = new ArrayList<>();
            for (ONode n1 : oResp.get("output").get("embeddings").ary()) {
                int index = 0;
                if (n1.contains("text_index")) {
                    index = n1.get("text_index").getInt();
                } else {
                    index = n1.get("index").getInt();
                }

                data.add(new Embedding(index, n1.get("embedding").toObject(float[].class)));
            }


            AiUsage usage = null;

            if (oResp.contains("usage")) {
                ONode oUsage = oResp.get("usage");
                int total_tokens = oUsage.get("total_tokens").getInt();
                usage = new AiUsage(
                        total_tokens,
                        0,
                        total_tokens
                );
            }

            return new EmbeddingResponse(model, null, data, usage);
        }
    }
}