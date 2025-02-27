package features.ai.repo.redis;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.noear.redisx.RedisClient;
import org.noear.solon.Solon;
import org.noear.solon.ai.embedding.EmbeddingConfig;
import org.noear.solon.ai.embedding.EmbeddingModel;
import org.noear.solon.ai.rag.Document;
import org.noear.solon.ai.rag.DocumentLoader;
import org.noear.solon.ai.rag.RepositoryStorable;
import org.noear.solon.ai.rag.loader.HtmlSimpleLoader;
import org.noear.solon.ai.rag.loader.MarkdownLoader;
import org.noear.solon.ai.rag.repository.RedisRepository;
import org.noear.solon.ai.rag.splitter.RegexTextSplitter;
import org.noear.solon.ai.rag.splitter.SplitterPipeline;
import org.noear.solon.ai.rag.splitter.TokenSizeTextSplitter;
import org.noear.solon.ai.rag.util.QueryCondition;
import org.noear.solon.net.http.HttpUtils;
import org.noear.solon.test.SolonTest;

import redis.clients.jedis.UnifiedJedis;

/**
 * @author noear 2025/2/26 created
 */
@SolonTest
public class RedisRepositoryTest {
    private RedisRepository repository;
    private UnifiedJedis client;


    @BeforeEach
    public void setup() throws IOException {
        // 创建测试用的 MockEmbeddingModel
        EmbeddingModel embeddingModel = new MockEmbeddingModel();

        // 创建 Redis 客户端
        RedisClient redisClient = Solon.cfg().getBean("solon.ai.repo.redis", RedisClient.class);
        if (redisClient == null) {
            throw new IllegalStateException("Redis client configuration not found!");
        }
        client = redisClient.openSession().jedis();

        // 创建测试用的 Repository
        repository = new RedisRepository(embeddingModel, client, "test_idx", "test_doc:");
        load(repository, "https://solon.noear.org/article/about?format=md");
        load(repository, "https://h5.noear.org/more.htm");
        load(repository, "https://h5.noear.org/readme.htm");
    }

    @AfterEach
    public void cleanup() {
        // 清理测试数据
        try {
            client.ftDropIndex("test_idx");
        } catch (Exception ignored) {
        }
        client.close();
    }

    @Test
    public void testStore() throws IOException {
        // 准备测试文档，显式指定 ID
        Document doc = new Document("test1", "Test content", new HashMap<>());
        repository.store(Collections.singletonList(doc));

        // 验证存储
        String key = "test_doc:" + doc.getId();
        assertTrue(client.exists(key), "Document should exist in Redis");
    }

    @Test
    public void testRemove() throws IOException {
        // 准备并存储文档，显式指定 ID
        Document doc = new Document("test2", "Test content", new HashMap<>());
        repository.store(Collections.singletonList(doc));
        String key = "test_doc:" + doc.getId();

        // 验证存储成功
        assertTrue(client.exists(key), "Document should exist after storing");

        // 删除文档
        repository.remove(doc.getId());

        // 验证删除成功
        assertFalse(client.exists(key), "Document should not exist after removal");
    }

    @Test
    public void testSearch() throws IOException {
        // 准备测试数据
        Document doc1 = new Document("doc1", "Solon is a lightweight Java web framework", new HashMap<>());
        Document doc2 = new Document("doc2", "Spring is a popular Java framework", new HashMap<>());

        repository.store(Arrays.asList(doc1, doc2));

        // 测试搜索
        QueryCondition condition = new QueryCondition("solon")
                .limit(10)
                .similarityThreshold(0.0f);  // 降低相似度阈值

        List<Document> results = repository.search(condition);

        // 验证结果
        assertFalse(results.isEmpty(), "搜索结果不应为空");
        assertTrue(results.size() <= condition.getLimit(), "结果数量应该符合限制");
        for (Document result : results) {
            System.out.println(result.getContent());
            assertTrue(result.getContent().toLowerCase().contains("solon"), "搜索结果应该包含查询关键词");
        }
    }

    private void load(RepositoryStorable repository, String url) throws IOException {
        String text = HttpUtils.http(url).get();

        //1.加载文档（测试用）
        DocumentLoader loader = null;
        if (text.contains("</html>")) {
            loader = new HtmlSimpleLoader(text.getBytes(StandardCharsets.UTF_8));
        } else {
            loader = new MarkdownLoader(text.getBytes(StandardCharsets.UTF_8));
        }

        List<Document> documents = new SplitterPipeline() //2.分割文档（确保不超过 max-token-size）
                .next(new RegexTextSplitter())
                .next(new TokenSizeTextSplitter(500))
                .split(loader.load());

        repository.store(documents); //（推入文档）
    }

    // 测试专用的 MockEmbeddingModel
    private static class MockEmbeddingModel extends EmbeddingModel {
        public MockEmbeddingModel() {
            super(new EmbeddingConfig());
        }

        @Override
        public float[] embed(String content) {
            return new float[]{0.1f, 0.2f, 0.3f};
        }

        @Override
        public void embed(List<Document> documents) {
            for (Document doc : documents) {
                doc.embedding(new float[]{0.1f, 0.2f, 0.3f});
            }
        }
    }
}
