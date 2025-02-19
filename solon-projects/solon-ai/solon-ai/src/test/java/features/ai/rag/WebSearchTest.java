package features.ai.rag;

import org.junit.jupiter.api.Test;
import org.noear.solon.ai.chat.ChatResponse;
import org.noear.solon.ai.rag.Document;
import org.noear.solon.ai.rag.Prompts;
import org.noear.solon.ai.rag.repository.WebSearchRepository;

import java.util.List;

/**
 * @author noear 2025/2/19 created
 */
public class WebSearchTest {
    @Test
    public void case1() throws Exception {
        WebSearchRepository repository = TestUtils.getWebSearchRepositoryOfBochaai();
        String query = "solon 是谁开发的？";

        List<Document> context = repository.search(query);

        ChatResponse resp = TestUtils.getChatModelOfGiteeai()
                .prompt(Prompts.augment(query, context))
                .call();

        //打印
        System.out.println(resp.getMessage());
    }
}
