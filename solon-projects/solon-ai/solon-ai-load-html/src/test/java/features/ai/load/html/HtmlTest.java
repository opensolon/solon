package features.ai.load.html;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.noear.solon.ai.rag.Document;
import org.noear.solon.ai.rag.loader.HtmlSimpleLoader;

/**
 * HtmlSimpleLoader 测试用例
 * 
 * @author noear 2025/2/21 created
 */
public class HtmlTest {
    private static final String TEST_URL = "https://solon.noear.org/article/about";
    private static final String TEST_URL2 = "https://solon.noear.org/article/start";

    @Test
    public void testSingleUrl() {
        // 测试单个URL加载
        HtmlSimpleLoader loader = new HtmlSimpleLoader(TEST_URL);
        List<Document> docs = loader.load();
        
        Assertions.assertFalse(docs.isEmpty(), "文档列表不应为空");
        Assertions.assertEquals(1, docs.size(), "应该只有一个文档");
        
        Document doc = docs.get(0);
        Assertions.assertNotNull(doc.getContent(), "文档内容不应为空");
        Assertions.assertEquals(TEST_URL, doc.getMetadata().get("source"), "源URL应匹配");
        for (Document d : docs) {
            System.out.println(d.getContent());
        }
    }

    @Test
    public void testMultipleUrls() {
        // 测试多个URL加载
        List<String> urls = Arrays.asList(TEST_URL, TEST_URL2);
        HtmlSimpleLoader loader = new HtmlSimpleLoader(urls, true); // 启用并发
        List<Document> docs = loader.load();
        
        Assertions.assertEquals(2, docs.size(), "应该有两个文档");
        Assertions.assertTrue(docs.stream()
                .allMatch(doc -> doc.getMetadata().containsKey("source")),
                "所有文档都应该有source元数据");
        for (Document d : docs) {
            System.out.println(d.getContent());
        }
    }

    @Test
    public void testCustomHeaders() {
        // 测试自定义请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("Custom-Header", "TestValue");
        
        HtmlSimpleLoader loader = new HtmlSimpleLoader.Builder(TEST_URL)
                .headers(headers)
                .build();
        
        List<Document> docs = loader.load();
        Assertions.assertFalse(docs.isEmpty(), "使用自定义请求头时文档加载应成功");
    }

    @Test
    public void testErrorHandling() {
        // 测试错误处理
        String invalidUrl = "http://invalid.url.test";
        
        // 测试错误继续
        HtmlSimpleLoader loader1 = new HtmlSimpleLoader.Builder(Arrays.asList(TEST_URL, invalidUrl))
                .continueOnFailure(true)
                .build();
        
        List<Document> docs = loader1.load();
        Assertions.assertEquals(1, docs.size(), "应该只加载成功的URL");

        // 测试错误抛出
        HtmlSimpleLoader loader2 = new HtmlSimpleLoader.Builder(invalidUrl)
                .continueOnFailure(false)
                .build();
        
        Assertions.assertThrows(RuntimeException.class, loader2::load,
                "无效URL应该抛出异常");
    }

    @Test
    public void testTimeout() {
        // 测试超时设置
        HtmlSimpleLoader loader = new HtmlSimpleLoader.Builder(TEST_URL)
                .timeout(1) // 设置一个很小的超时值
                .continueOnFailure(true)
                .build();
        
        List<Document> docs = loader.load();
        Assertions.assertTrue(docs.isEmpty(), "超时应该导致加载失败");
    }

    @Test
    public void testConcurrentVsSync() {
        List<String> urls = Arrays.asList(TEST_URL, TEST_URL2);
        
        // 测试同步加载
        HtmlSimpleLoader syncLoader = new HtmlSimpleLoader(urls, false);
        List<Document> syncDocs = syncLoader.load();
        
        // 测试并发加载
        HtmlSimpleLoader concurrentLoader = new HtmlSimpleLoader(urls, true);
        List<Document> concurrentDocs = concurrentLoader.load();
        
        // 两种方式应该加载相同数量的文档
        Assertions.assertEquals(syncDocs.size(), concurrentDocs.size(),
                "同步和并发加载应该产生相同数量的文档");
    }
}
