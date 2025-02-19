package org.noear.solon.ai.chat.dialect;

import org.noear.snack.ONode;
import org.noear.solon.Utils;
import org.noear.solon.ai.AiUsage;
import org.noear.solon.ai.chat.*;
import org.noear.solon.ai.chat.message.AssistantMessage;
import org.noear.solon.ai.chat.message.ChatMessage;
import org.noear.solon.ai.chat.message.ToolMessage;

import java.util.Date;
import java.util.List;

/**
 * DashScope 方言（阿里云产品）
 *
 * @author noear
 * @since 3.1
 */

public class DashscopeDialect extends AbstractChatDialect {

    private static DashscopeDialect instance = new DashscopeDialect();

    public static DashscopeDialect getInstance() {
        return instance;
    }

    @Override
    public boolean matched(ChatConfig config) {
        return "dashscope".equals(config.getProvider());

    }


    //    @Override
    @Override
    public String buildRequestJson(ChatConfig config, ChatOptions options, List<ChatMessage> messages, boolean stream) {
        return new ONode().build(n -> {
            n.set("stream", stream);

            if (options.option("temperature") != null) {
                n.set("temperature", options.option("temperature"));
            }

            if (Utils.isNotEmpty(config.getModel())) {
                n.set("model", config.getModel());
            }

            ONode inputNode = new ONode();
            n.setNode("input", inputNode);

            inputNode.getOrNew("messages").build(n1 -> {
                for (ChatMessage m1 : messages) {
                    n1.add(buildChatMessageNode(m1));
                }
            });

            ONode p = new ONode();
            n.setNode("parameters", p);
            p.set("result_format","message");
            buildReqFunctionsNodeDo(p, config.getGlobalFunctions());
            buildReqFunctionsNodeDo(p, options.functions());

        }).toJson();
    }


    @Override
    public boolean parseResponseJson(ChatConfig config, ChatResponseDefault resp, String json) {
        if (json.startsWith("data:")) {
            json = json.substring(6);
        }

        //解析
        ONode oResp = ONode.load(json);

        if (oResp.isObject() == false) {
            return false;
        }


        if (oResp.contains("code")&&!Utils.isEmpty(oResp.get("code").getString())) {
            resp.setError(new ChatException(oResp.get("code").getString()+"\t"+oResp.get("message").getString()));
//            log.debug("请参考文档：https://help.aliyun.com/zh/model-studio/developer-reference/error-code");
        } else {
            ONode output = oResp.get("output");
            ONode choices = output.get("choices");

            resp.setModel(config.getModel());
            boolean finishReason = false;
            int i=0;
            Date created=null;
            for (ONode oChoice1 : choices.ary()) {

                int index = i++;
                String finish_reason=choices.get("finish_reason").getString();
                AssistantMessage message1;
                if (oChoice1.contains("delta")) {  //object=chat.completion.chunk
                    message1 = parseAssistantMessage(resp, oChoice1.get("delta"));
                } else { //object=chat.completion
                    message1 = parseAssistantMessage(resp, oChoice1.get("message"));
                }
                resp.addChoice(new ChatChoice(index, created, finish_reason, message1));

            }


            ONode oUsage = oResp.getOrNull("usage");
            if (oUsage != null) {
                long promptTokens = oUsage.get("input_tokens").getLong();
                long completionTokens = oUsage.get("output_tokens").getLong();
                long totalTokens = oUsage.get("total_tokens").getLong();

                resp.setUsage(new AiUsage(promptTokens, completionTokens, totalTokens));
            }
        }

        return true;
    }
}
