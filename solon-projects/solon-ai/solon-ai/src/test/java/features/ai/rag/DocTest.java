package features.ai.rag;

import features.ai.chat.OpenaiTest;
import org.junit.jupiter.api.Test;
import org.noear.solon.ai.chat.ChatModel;
import org.noear.solon.ai.chat.ChatResponse;
import org.noear.solon.ai.chat.message.ChatMessage;
import org.noear.solon.ai.embedding.EmbeddingModel;
import org.noear.solon.ai.rag.Document;
import org.noear.solon.ai.rag.repository.Repository;
import org.noear.solon.ai.rag.repository.SimpleRepository;
import org.noear.solon.ai.rag.splitter.TokenTextSplitter;
import org.noear.solon.net.http.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author noear 2025/2/18 created
 */
public class DocTest {
    //JQC6M0GTNPGSCEXZOBUGUX0HVHCOLDIMN6XOSSSA
    private static final Logger log = LoggerFactory.getLogger(OpenaiTest.class);


    @Test
    public void rag_case1() throws Exception {
        String text = HttpUtils.http("https://solon.noear.org/article/about?format=md").get(); //1.加载文档（测试用）

        List<Document> documents = new TokenTextSplitter().split(text); //2.分割文档（确保不超过 max-token-size）
        //System.out.println(documents);

        SimpleRepository repository = new SimpleRepository(getEmbeddingModel()); //3.初始化知识库
        repository.put(documents); //（推入文档）

        ChatModel chatModel = getChatModel();

        qa("Solon 是谁开发的？", repository, chatModel); //5.应用
    }

    private void qa(String question, Repository repository, ChatModel chatModel) throws Exception {
        List<Document> context = repository.search(question); //1.搜索知识库（结果，作为问题的上下文）
        System.out.println(context);

        String prompt = String.format("问题：%s\n\n 参考以下内容回答：\n%s", question, context); //2.增强问题提示语

        ChatResponse resp = chatModel.prompt(ChatMessage.ofUser(prompt)).call(); //3.调用大模型

        //打印
        System.out.println(resp.getMessage());
    }

    private ChatModel getChatModel() {
        final String apiUrl = "https://api.deepseek.com/v1/chat/completions";
        final String apkKey = "sk-9f4415ddc570496581897c22e3d41a54";
        final String model = "deepseek-chat"; //deepseek-reasoner//deepseek-chat

        return ChatModel.of(apiUrl).apiKey(apkKey).model(model).build(); //4.初始化语言模型
    }

    private EmbeddingModel getEmbeddingModel() {
        final String apiUrl = "https://ai.gitee.com/v1/embeddings";
        final String apkKey = "JQC6M0GTNPGSCEXZOBUGUX0HVHCOLDIMN6XOSSSA";
        final String provider = "giteeai";
        final String model = "bge-m3";//

        return EmbeddingModel.of(apiUrl).apiKey(apkKey).provider(provider).model(model).build();
    }
}
