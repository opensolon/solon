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
import org.noear.solon.Utils;
import org.noear.solon.ai.image.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * DashScope 图型模型方言（阿里云产品）
 *
 * @author noear
 * @since 3.1
 */

public class DashscopeImageDialect extends AbstractImageDialect {
    //https://help.aliyun.com/zh/model-studio/developer-reference

    private static DashscopeImageDialect instance = new DashscopeImageDialect();

    public static DashscopeImageDialect getInstance() {
        return instance;
    }

    /**
     * 匹配检测
     *
     * @param config 聊天配置
     */
    @Override
    public boolean matched(ImageConfig config) {
        return "dashscope".equals(config.getProvider());

    }

    @Override
    public String buildRequestJson(ImageConfig config, ImageOptions options, String prompt) {
        return new ONode().build(n -> {
            if (Utils.isNotEmpty(config.getModel())) {
                n.set("model", config.getModel());
            }

            if (Utils.isNotEmpty(prompt)) {
                n.getOrNew("input").set("prompt", prompt);
            }

            n.getOrNew("parameters").build(n1 -> {
                for (Map.Entry<String, Object> kv : options.options().entrySet()) {
                    n1.set(kv.getKey(), kv.getValue());
                }
            });
        }).toJson();
    }

    @Override
    public ImageResponse parseResponseJson(ImageConfig config, String respJson) {
        ONode oResp = ONode.load(respJson);

        String model = oResp.get("model").getString();


        //https://dashscope.aliyuncs.com/api/v1/tasks/

        if (oResp.contains("code") && !Utils.isEmpty(oResp.get("code").getString())) {
            return new ImageResponse(model, new ImageException(oResp.get("code").getString() + ": " + oResp.get("message").getString()), null, null);
        } else {
            List<Image> data = null;
            ONode oOutput = oResp.get("output");

            if (oOutput.contains("results")) {
                //同步模式直接有结果
                data = oOutput.get("results").toObjectList(Image.class);
            } else {
                //异步模式只返回任务 id
                String url = "https://dashscope.aliyuncs.com/api/v1/tasks/" + oOutput.get("task_id").getString();
                data = Arrays.asList(Image.ofUrl(url));
            }

            return new ImageResponse(model, null, data, null);
        }
    }
}