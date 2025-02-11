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
import org.noear.solon.ai.chat.ChatException;
import org.noear.solon.ai.chat.*;
import org.noear.solon.ai.chat.ChatResponseImpl;
import org.noear.solon.core.util.DateUtil;

import java.util.Date;

/**
 * Ollama 方言
 *
 * @author noear
 * @since 3.1
 */
public class OllamaDialect extends AbstractDialect {
    private static OllamaDialect instance = new OllamaDialect();

    public static OllamaDialect getInstance() {
        return instance;
    }

    @Override
    public boolean matched(ChatConfig config) {
        return "ollama".equals(config.provider());
    }

    @Override
    public boolean parseResponseJson(ChatConfig config, ChatResponseImpl resp, String json) {
        //解析
        ONode oResp = ONode.load(json);

        if (oResp.isObject() == false) {
            return false;
        }

        if (oResp.contains("error")) {
            resp.exception = new ChatException(oResp.get("error").getString());
        } else {
            resp.model = oResp.get("model").getString();
            resp.finished = oResp.get("done").getBoolean();
            String done_reason = oResp.get("done_reason").getString();

            String createdStr = oResp.get("created_at").getString();
            if (createdStr != null) {
                createdStr = createdStr.substring(0, createdStr.indexOf(".") + 4);
            }
            Date created = DateUtil.parseTry(createdStr);
            resp.choices.clear();
            resp.choices.add(new ChatChoice(0, created, done_reason, parseAssistantMessage(oResp.get("message"))));

            if (resp.finished) {
                long promptTokens = oResp.get("prompt_eval_count").getLong();
                long completionTokens = oResp.get("eval_count").getLong();
                long totalTokens = promptTokens + completionTokens;

                resp.usage = new ChatUsage(promptTokens, completionTokens, totalTokens);
            }
        }

        return true;
    }
}