package org.noear.solon.ai.chat.dialect;

import org.noear.snack.ONode;
import org.noear.solon.ai.AiException;
import org.noear.solon.ai.chat.ChatConfig;
import org.noear.solon.ai.chat.ChatMessage;
import org.noear.solon.ai.chat.impl.ChatResponseDefault;

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
            resp.exception = new AiException(oResp.get("error").getString());
        } else {
            resp.model = oResp.get("model").getString();
            resp.created_at = oResp.get("created").getString();

            ONode oChoice1 = oResp.get("choices").get(0);

            if (oChoice1.contains("delta")) {
                resp.message = ChatMessage.of(config, oChoice1.get("delta"));
            } else {
                resp.message = ChatMessage.of(config, oChoice1.get("message"));
            }

            resp.done = oResp.contains("usage");
            resp.done_reason = oResp.get("finish_reason").getString();
        }

        return true;
    }
}
