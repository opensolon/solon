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
package org.noear.solon.ai.chat.dialect;

import org.noear.snack.ONode;
import org.noear.solon.ai.AiException;
import org.noear.solon.ai.chat.*;
import org.noear.solon.ai.chat.ChatResponseDefault;

import java.util.Date;

/**
 * Openai 方言
 *
 * @author noear
 * @since 3.1
 */
public class OpenaiDialect extends AbstractDialect {
    private static final OpenaiDialect instance = new OpenaiDialect();

    public static OpenaiDialect instance() {
        return instance;
    }

    @Override
    public boolean matched(ChatConfig config) {
        return false;
    }

    @Override
    protected void buildReqFunctionsJson(ONode n, ChatConfig config, ChatOptions options, ChatMessage lastMessage) {
        if (lastMessage.getRole() != ChatRole.TOOL) {
            //如果是 tool ，后面不跟 funcs
            buildReqFunctionsJsonDo(n, config.globalFunctions());
            buildReqFunctionsJsonDo(n, options.functions());
        }
    }

    @Override
    public boolean resolveResponseJson(ChatConfig config, ChatResponseDefault resp, String json) {
        if (json.startsWith("data:")) {
            json = json.substring(6);

            if("[DONE]".equals(json)){ //不是数据结构
                resp.finished=true;
                return true;
            }
        }

        //解析
        ONode oResp = ONode.load(json);

        if (oResp.isObject() == false) {
            return false;
        }

        if (oResp.contains("error")) {
            resp.exception = new AiException(oResp.get("error").get("message").getString());
        } else {
            resp.model = oResp.get("model").getString();
            resp.finished = oResp.contains("usage");

            String finish_reason = oResp.get("finish_reason").getString();
            Date created = new Date(oResp.get("created").getLong() * 1000);

            for (ONode oChoice1 : oResp.get("choices").ary()) {
                int index = oChoice1.get("index").getInt();

                if (oChoice1.contains("delta")) {
                    //object=chat.completion.chunk
                    resp.choices.add(new ChatChoice(index, created, finish_reason, ChatMessage.ofAssistant(config, oChoice1.get("delta"))));
                } else {
                    //object=chat.completion
                    resp.choices.add(new ChatChoice(index, created, finish_reason, ChatMessage.ofAssistant(config, oChoice1.get("message"))));
                }
            }
        }

        return true;
    }
}