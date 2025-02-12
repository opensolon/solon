package org.noear.solon.ai.rag;

import org.noear.solon.ai.chat.ChatModel;
import org.noear.solon.ai.chat.ChatRequest;
import org.noear.solon.ai.chat.functioncall.ChatFunction;
import org.noear.solon.ai.rag.loader.DocumentLoader;
import org.noear.solon.flow.FlowEngine;

/**
 * @author noear 2025/2/12 created
 */
public interface Agent {
    static Agent of() {
        return null;
    }

    Agent model(ChatModel chatModel);
    Agent repository(Repository repository);
    Agent flow(FlowEngine flowEngine);
    Agent build();


    ChatRequest prompt(String prompt);

    void load(DocumentLoader loader);

    void load(ChatFunction function);
}
