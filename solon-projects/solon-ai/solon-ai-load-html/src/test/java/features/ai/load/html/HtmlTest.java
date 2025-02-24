package features.ai.load.html;

import java.net.URI;
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

    @Test
    public void testSingleUrl() throws Exception {
        // 测试单个URL加载
        HtmlSimpleLoader loader = new HtmlSimpleLoader(URI.create(TEST_URL).toURL());
        List<Document> docs = loader.load();

        Assertions.assertFalse(docs.isEmpty(), "文档列表不应为空");
        Assertions.assertEquals(1, docs.size(), "应该只有一个文档");

        Document doc = docs.get(0);
        Assertions.assertNotNull(doc.getContent(), "文档内容不应为空");
        for (Document d : docs) {
            System.out.println(d.getContent());
        }
    }
}
