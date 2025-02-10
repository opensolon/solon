package org.noear.solon.ai.chat.dialect;

import org.noear.snack.ONode;
import org.noear.solon.ai.AiException;
import org.noear.solon.ai.chat.ChatChoice;
import org.noear.solon.ai.chat.ChatConfig;
import org.noear.solon.ai.chat.ChatMessage;
import org.noear.solon.ai.chat.impl.ChatResponseDefault;

import java.util.Date;

/**
 * @author noear 2025/2/9 created
 */
public class OpenaiDialect extends AbstractDialect {
    private static final OpenaiDialect instance = new OpenaiDialect();
    public static OpenaiDialect instance() {
        return instance;
    }

    @Override
    public String provider() {
        return "openai";
    }

    @Override
    public boolean resolveResponseJson(ChatConfig config, ChatResponseDefault resp, String json) {
        if (json.startsWith("data:")) {
            json = json.substring(6);
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
            resp.done_reason = oResp.get("finish_reason").getString();

            resp.choices.clear();

            Date created = new Date(oResp.get("created").getLong() * 1000);

            for(ONode oChoice1 : oResp.get("choices").ary()) {
                int index = oChoice1.get("index").getInt();

                if (oChoice1.contains("delta")) {
                    //object=chat.completion.chunk
                    resp.choices.add(new ChatChoice(index,ChatMessage.ofAssistant(config, created, oChoice1.get("delta"))));
                } else {
                    //object=chat.completion
                    resp.choices.add(new ChatChoice(index,ChatMessage.ofAssistant(config, created, oChoice1.get("message"))));
                }
            }
        }

        return true;
    }
}
