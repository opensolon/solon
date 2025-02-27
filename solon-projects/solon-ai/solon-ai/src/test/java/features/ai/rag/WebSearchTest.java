package features.ai.rag;

import org.junit.jupiter.api.Test;
import org.noear.solon.ai.chat.ChatResponse;
import org.noear.solon.ai.chat.message.ChatMessage;
import org.noear.solon.ai.rag.Document;
import org.noear.solon.ai.rag.repository.WebSearchRepository;

import java.util.List;

/**
 * @author noear 2025/2/19 created
 */
public class WebSearchTest {
    @Test
    public void case1() throws Exception {
        WebSearchRepository repository = TestUtils.getWebSearchRepository();
        String query = "solon 是谁开发的？";

        List<Document> context = repository.search(query);

        ChatResponse resp = TestUtils.getChatModel()
                .prompt(ChatMessage.augment(query, context))
                .call();

        //打印
        System.out.println(resp.getMessage());
    }

    @Test
    public void case2() throws Exception {
        WebSearchRepository repository = TestUtils.getWebSearchRepository();
        String query = "solon 是谁开发的？";

        List<Document> context = repository.search(query);

        ChatResponse resp = TestUtils.getChatModel()
                .prompt(ChatMessage.template("${query} \n\n 请参考以下内容回答：${context}")
                        .param("query", query)
                        .param("context", context)
                        .generate())
                .call();

        //打印
        System.out.println(resp.getMessage());
    }
}
