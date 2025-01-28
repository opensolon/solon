package demo.ai;

import org.noear.solon.ai.chat.ChatMessage;
import org.noear.solon.ai.chat.ChatModel;
import org.noear.solon.ai.chat.ChatOptions;
import org.noear.solon.ai.chat.ChatResponse;

/**
 * @author noear 2025/1/28 created
 */
public class DemoTest {
    public void case1() {
        ChatModel chatModel = ChatModel.of(null);

        //一次性返回
        {
            ChatResponse resp = chatModel
                    .prompt(ChatMessage.ofUser("xxx"))
                    .options(ChatOptions.of().temperature(0.8F))
                    .call();
        }

        //流返回(sse)
        {

            chatModel.prompt(ChatMessage.ofUser("yyy"))
                    .stream(resp -> {

                    });
        }
    }
}
