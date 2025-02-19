package features.ai.rag;

import features.ai.chat.OpenaiTest;
import org.junit.jupiter.api.Test;
import org.noear.solon.ai.chat.ChatModel;
import org.noear.solon.ai.chat.ChatResponse;
import org.noear.solon.ai.rag.Document;
import org.noear.solon.ai.rag.Prompts;
import org.noear.solon.ai.rag.repository.Repository;
import org.noear.solon.ai.rag.repository.RepositoryStorable;
import org.noear.solon.ai.rag.repository.InMemoryRepository;
import org.noear.solon.ai.rag.splitter.TokenTextSplitter;
import org.noear.solon.net.http.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * @author noear 2025/2/18 created
 */
public class RagRepositoryTest {
    //JQC6M0GTNPGSCEXZOBUGUX0HVHCOLDIMN6XOSSSA
    private static final Logger log = LoggerFactory.getLogger(OpenaiTest.class);


    @Test
    public void rag_case1() throws Exception {
        //1.构建模型
        ChatModel chatModel = TestUtils.getChatModelOfGiteeai();

        //2.构建知识库
        InMemoryRepository repository = new InMemoryRepository(TestUtils.getEmbeddingModelOfGiteeai()); //3.初始化知识库
        load(repository, "https://solon.noear.org/article/about?format=md");
        load(repository, "https://h5.noear.org/more.htm");
        load(repository, "https://h5.noear.org/readme.htm");

        //3.应用
        qa("Solon 是谁开发的？", repository, chatModel); //5.应用
    }

    private void load(RepositoryStorable repository, String url) throws IOException {
        String text = HttpUtils.http(url).get(); //1.加载文档（测试用）
        List<Document> documents = new TokenTextSplitter(200).split(text); //2.分割文档（确保不超过 max-token-size）
        repository.put(documents); //（推入文档）
    }

    private void qa(String question, Repository repository, ChatModel chatModel) throws Exception {
        List<Document> context = repository.search(question); //1.搜索知识库（结果，作为问题的上下文）
        ChatResponse resp = chatModel.prompt(Prompts.augment(question, context)).call(); //3.调用大模型

        //打印
        System.out.println(resp.getMessage());
    }
}
