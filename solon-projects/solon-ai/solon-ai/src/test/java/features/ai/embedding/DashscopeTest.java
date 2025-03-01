package features.ai.embedding;

import org.junit.jupiter.api.Test;
import org.noear.solon.ai.embedding.EmbeddingModel;
import org.noear.solon.ai.embedding.EmbeddingResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author noear 2025/3/1 created
 */
public class DashscopeTest {
    private static final Logger log = LoggerFactory.getLogger(features.ai.chat.DashscopeTest.class);
    private static final String apiUrl = "https://dashscope.aliyuncs.com/api/v1/services/embeddings/text-embedding/text-embedding";
    private static final String apiKey = "sk-1ffe449611a74e61ad8e71e1b35a9858";
    private static final String provider = "dashscope";
    private static final String model = "text-embedding-v3";//"llama3.2"; //deepseek-r1:1.5b;


    @Test
    public void case1() throws IOException {
        EmbeddingModel embeddingModel = EmbeddingModel.of(apiUrl)
                .apiKey(apiKey)
                .provider(provider) //需要指定供应商，用于识别接口风格（也称为方言）
                .model(model)
                .build();

        //一次性返回
        EmbeddingResponse resp = embeddingModel
                .input("比较原始的风格", "能表达内在的大概过程", "太阳升起来了")
                .call();

        //打印消息
        log.warn("{}", resp.getData());
    }
}