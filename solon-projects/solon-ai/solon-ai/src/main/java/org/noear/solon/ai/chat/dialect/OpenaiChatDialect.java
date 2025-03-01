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
import org.noear.solon.ai.AiUsage;
import org.noear.solon.ai.chat.ChatException;
import org.noear.solon.ai.chat.*;
import org.noear.solon.ai.chat.message.AssistantMessage;
import org.noear.solon.ai.chat.message.ChatMessage;

import java.util.Date;
import java.util.List;

/**
 * Openai 聊天模型方言
 *
 * @author noear
 * @since 3.1
 */
public class OpenaiChatDialect extends AbstractChatDialect {
    private static final OpenaiChatDialect instance = new OpenaiChatDialect();

    public static OpenaiChatDialect instance() {
        return instance;
    }

    /**
     * 匹配检测
     *
     * @param config 聊天配置
     */
    @Override
    public boolean matched(ChatConfig config) {
        return false;
    }

    @Override
    protected void buildReqFunctionsNode(ONode n, ChatConfig config, ChatOptions options, ChatMessage lastMessage) {
        if (lastMessage.getRole() != ChatRole.TOOL) {
            //如果是 tool ，后面不跟 funcs
            buildReqFunctionsNodeDo(n, config.getGlobalFunctions());
            buildReqFunctionsNodeDo(n, options.functions());
        }
    }

    @Override
    public boolean parseResponseJson(ChatConfig config, boolean isStream, ChatResponseDefault resp, String json) {
        if (json.startsWith("data:")) {
            json = json.substring(6);

            if ("[DONE]".equals(json)) { //不是数据结构
                resp.setFinished(true);
                return true;
            }
        }

        //解析
        ONode oResp = ONode.load(json);

        if (oResp.isObject() == false) {
            return false;
        }

        if (oResp.contains("error")) {
            resp.setError(new ChatException(oResp.get("error").getString()));
        } else {
            resp.setModel(oResp.get("model").getString());

            String finish_reason = oResp.get("finish_reason").getString();
            Date created = new Date(oResp.get("created").getLong() * 1000);

            for (ONode oChoice1 : oResp.get("choices").ary()) {
                int index = oChoice1.get("index").getInt();

                List<AssistantMessage> messageList;
                if (oChoice1.contains("delta")) {  //object=chat.completion.chunk
                    messageList = parseAssistantMessage(isStream, resp, oChoice1.get("delta"));
                } else { //object=chat.completion
                    messageList = parseAssistantMessage(isStream, resp, oChoice1.get("message"));
                }

                for (AssistantMessage msg1 : messageList) {
                    resp.addChoice(new ChatChoice(index, created, finish_reason, msg1));
                }
            }

            ONode oUsage = oResp.getOrNull("usage");
            if (oUsage != null) {
                long promptTokens = oUsage.get("prompt_tokens").getLong();
                long completionTokens = oUsage.get("completion_tokens").getLong();
                long totalTokens = oUsage.get("total_tokens").getLong();

                resp.setUsage(new AiUsage(promptTokens, completionTokens, totalTokens));
            }
        }

        return true;
    }
}