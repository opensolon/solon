package features.ai.repo.elasticsearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.noear.solon.ai.embedding.EmbeddingConfig;
import org.noear.solon.ai.embedding.EmbeddingModel;
import org.noear.solon.ai.rag.Document;
import org.noear.solon.ai.rag.RepositoryStorable;
import org.noear.solon.ai.rag.repository.ElasticsearchRepository;
import org.noear.solon.ai.rag.splitter.TokenSizeTextSplitter;
import org.noear.solon.ai.rag.util.QueryCondition;
import org.noear.solon.net.http.HttpUtils;

/**
 * ElasticsearchRepository 测试类
 *
 * @author 小奶奶花生米
 * @since 3.1
 */
public class ElasticsearchRepositoryTest {
    private ElasticsearchRepository repository;
    private RestHighLevelClient client;
    private static final String TEST_INDEX = "test_docs";

    @BeforeEach
    public void setup() throws IOException, InterruptedException {
        // 创建ES客户端
        client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("127.0.0.1", 9200, "http")));

        // 创建一个简单的 EmbeddingModel 实现用于测试
        EmbeddingModel embeddingModel = new EmbeddingModel(new EmbeddingConfig()) {
            @Override
            public float[] embed(String content) {
                // 返回一个简单的测试向量
                return new float[]{0.1f, 0.2f, 0.3f};
            }
        };

        repository = new ElasticsearchRepository(embeddingModel, client, TEST_INDEX);

        // 初始化测试数据
        repository.delete("*");  // 清空所有文档
        load(repository, "https://solon.noear.org/article/about?format=md");
        load(repository, "https://h5.noear.org/more.htm");
        load(repository, "https://h5.noear.org/readme.htm");
        Thread.sleep(1000);
    }

    @Test
    public void testSearch() throws IOException {
        try {

            // 测试基本搜索
            QueryCondition condition = new QueryCondition("solon");
            List<Document> results = repository.search(condition);
            assertFalse(results.isEmpty(), "应该找到包含solon的文档");

            // 测试带过滤器的搜索
            condition = new QueryCondition("solon")
                    .filter(doc -> doc.getUrl().contains("noear.org"));
            results = repository.search(condition);
            assertFalse(results.isEmpty(), "应该找到noear.org域名下的文档");
            assertTrue(results.get(0).getUrl().contains("noear.org"), "文档URL应该包含noear.org");

            // 打印结果
            for (Document doc : results) {
                System.out.println(doc.getId() + ":" + doc.getScore() + ":" + doc.getUrl() + "【" + doc.getContent() + "】");
            }

        } catch (Exception e) {
            fail("测试过程中发生异常: " + e.getMessage());
        }
    }

    @Test
    public void testRemove() {
        // 准备并存储测试数据
        List<Document> documents = new ArrayList<>();
        Document doc = new Document("Document to be removed", new HashMap<>());
        documents.add(doc);

        try {
            repository.insert(documents);
            Thread.sleep(1000);
            // 删除文档
            repository.delete(doc.getId());

            Thread.sleep(1000);
            // 验证文档已被删除
            QueryCondition condition = new QueryCondition("removed");
            List<Document> results = repository.search(condition);
            assertTrue(results.isEmpty(), "文档应该已被删除");

        } catch (Exception e) {
            e.printStackTrace();
            fail("测试过程中发生异常: " + e.getMessage());
        }
    }

    @Test
    public void testSearchWithLimit() throws IOException {
        try {

            // 测试限制返回数量
            QueryCondition condition = new QueryCondition("solon")
                    .limit(2);  // 限制只返回2条结果
            List<Document> results = repository.search(condition);

            assertEquals(2, results.size(), "应该只返回2条文档");

            // 打印结果
            for (Document doc : results) {
                System.out.println(doc.getId() + ":" + doc.getScore() + ":" + doc.getUrl() + "【" + doc.getContent() + "】");
            }

        } catch (Exception e) {
            fail("测试过程中发生异常: " + e.getMessage());
        }
    }

    @Test
    public void testSearchScenarios() throws IOException {
        try {
            // 1. 测试空查询
            QueryCondition emptyCondition = new QueryCondition("");
            List<Document> results = repository.search(emptyCondition);
            assertFalse(results.isEmpty(), "空查询应该返回所有文档");

            // 2. 测试精确词语匹配
            QueryCondition exactCondition = new QueryCondition("杭州");
            results = repository.search(exactCondition);
            assertTrue(results.stream()
                    .anyMatch(doc -> doc.getContent().toLowerCase().contains("杭州")),
                    "应该能找到包含杭州的文档");

            // 3. 测试中文查询
            QueryCondition chineseCondition = new QueryCondition("框架");
            results = repository.search(chineseCondition);
            assertTrue(results.stream()
                    .anyMatch(doc -> doc.getContent().contains("框架")),
                    "应该能找到包含'框架'的文档");

            // 4. 测试组合过滤条件
            QueryCondition combinedCondition = new QueryCondition("solon")
                    .filter(doc -> doc.getUrl().contains("noear.org"))
                    .limit(5);
            results = repository.search(combinedCondition);
            assertTrue(results.size() <= 5, "返回结果不应超过限制数量");
            assertTrue(results.stream()
                    .allMatch(doc -> doc.getUrl().contains("noear.org")),
                    "所有结果都应该来自noear.org");

            // 5. 测试按相关性排序
            QueryCondition relevanceCondition = new QueryCondition("java web framework");
            results = repository.search(relevanceCondition);
            if (results.size() >= 2) {
                assertTrue(results.get(0).getScore() >= results.get(1).getScore(),
                        "结果应该按相关性降序排序");
            }

            // 打印所有测试结果
            System.out.println("\n=== 搜索测试结果 ===");
            for (Document doc : results) {
                System.out.println("Score: " + doc.getScore());
                System.out.println("URL: " + doc.getUrl());
                System.out.println("Content: " + doc.getContent());
                System.out.println("---");
            }

        } catch (Exception e) {
            fail("测试过程中发生异常: " + e.getMessage());
        }
    }

    private void load(RepositoryStorable repository, String url) throws IOException {
        String text = HttpUtils.http(url).get(); // 加载文档

        // 分割文档
        List<Document> documents = new TokenSizeTextSplitter(200).split(text).stream()
                .map(doc -> {
                    doc.url(url);
                    return doc;
                })
                .collect(Collectors.toList());

        // 存储文档
        repository.insert(documents);
    }
}
