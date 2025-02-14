package features.ai.embedding;

import org.noear.solon.ai.chat.ChatConfig;
import org.noear.solon.ai.chat.ChatModel;
import org.noear.solon.ai.chat.message.ChatMessage;
import org.noear.solon.ai.rag.*;
import org.noear.solon.ai.rag.loader.PdfDocumentLoader;
import org.noear.solon.flow.FlowEngine;

import java.util.List;

/**
 * @author noear 2025/2/12 created
 */
public class DemoTest {
    public static void rag_base() throws Exception {
        //模型
        ChatModel chatModel = ChatModel.of(new ChatConfig()).build();
        //知识库（向量存储与索引）
        Repository repository = new InMemoryRepository(EmbeddingModel.of(new EmbeddingConfig()));

        //加载知识
        repository.load(new PdfDocumentLoader("/.../神王传说.pdf"));

        //检索知识
        State response = repository.retrieve("神王世界的小草长啥样的？");

        //增强提示识
        ChatMessage message = Prompts.augment(response);

        //生成结果
        chatModel.prompt(message)
                .call();
    }

    public static void rag_agent() throws Exception {
        //模型
        ChatModel chatModel = ChatModel.of(new ChatConfig()).build();
        //知识库（向量存储与索引）
        Repository repository = new InMemoryRepository(EmbeddingModel.of(new EmbeddingConfig()));

        //智能体
        Agent agent = Agent.of()
                .model(chatModel) //配置模型
                .repository(repository) //配置知识库
                .flow(FlowEngine.newInstance()) //配置流引擎
                .build();

        //加载知识
        agent.load(new PdfDocumentLoader("/.../神王传说.pdf"));
        //加载功能（function call）
        agent.load(new OpsChatFunction());

        //聊天
        agent.prompt("神王世界的小草长啥样的？")
                .call();

        agent.prompt("运维指令：重启杭州东2区 ip 为168.0.1.121 的服务器")
                .call();

        agent.prompt("执行 flow1 业务流，抓取全网关于神王传说的知识")
                .call();
    }
}
