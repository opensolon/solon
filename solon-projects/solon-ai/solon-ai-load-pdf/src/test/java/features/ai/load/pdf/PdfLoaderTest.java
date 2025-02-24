package features.ai.load.pdf;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Assertions; // 修改导入路径
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.noear.solon.ai.rag.Document;
import org.noear.solon.ai.rag.loader.PdfLoader;

/**
 * PdfLoader 测试类
 *
 * @author 小奶奶花生米
 * @since 3.1
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PdfLoaderTest {
    private File pdfFile;

    @BeforeAll
    public void setup() throws UnsupportedEncodingException {
        // 获取测试PDF文件路径
        String pdfPath = PdfLoaderTest.class.getClassLoader()
                .getResource("PdfLoaderTest.pdf").getFile();
        // URL解码，处理中文和特殊字符
        pdfPath = URLDecoder.decode(pdfPath, StandardCharsets.UTF_8.name());
        pdfFile = new File(pdfPath);

        // 确认文件存在
        assertTrue(pdfFile.exists(), "PDF文件不存在: " + pdfFile.getAbsolutePath());
        assertTrue(pdfFile.length() > 0, "PDF文件为空");

        System.out.println("PDF文件路径: " + pdfFile.getAbsolutePath());
        System.out.println("文件大小: " + pdfFile.length() / 1024 + "KB");
    }

    /**
     * 测试分页模式
     */
    @Test
    public void testPageMode() throws IOException {
        PdfLoader loader = new PdfLoader(pdfFile);
        loader.additionalMetadata("title", pdfFile.getName());
        List<Document> documents = loader.load();

        // 验证基本信息
        assertNotNull(documents, "文档列表不应为空");
        assertTrue(documents.size() > 0, "文档页数应大于0");

        System.out.println("总页数: " + documents.size());

        // 打印前10页的内容预览
        for (int i = 0; i < Math.min(10, documents.size()); i++) {
            Document doc = documents.get(i);

            // 验证每页的元数据
            Assertions.assertNotNull(doc.getMetadata().get("title"), "文档标题不应为空");
            Assertions.assertEquals(i + 1, doc.getMetadata().get("page"), "页码应匹配");
            Assertions.assertEquals(documents.size(), doc.getMetadata().get("total_pages"), "总页数应匹配");
            Assertions.assertNotNull(doc.getContent(), "文档内容不应为空");

            System.out.println("\n第" + (i + 1) + "页:");
            System.out.println("标题: " + doc.getMetadata().get("title"));
            System.out.println("页码: " + doc.getMetadata().get("page"));
            System.out.println("总页数: " + doc.getMetadata().get("total_pages"));
            System.out.println("内容预览: " + doc.getContent());
        }
    }

    /**
     * 测试单文档模式
     */
    @Test
    public void testSingleMode() throws IOException {
        PdfLoader loader = new PdfLoader(pdfFile)
                .options(o -> o.loadMode(PdfLoader.LoadMode.SINGLE));
        loader.additionalMetadata("title", pdfFile.getName());
        List<Document> documents = loader.load();

        // 验证基本信息
        assertNotNull(documents, "文档列表不应为空");
        assertEquals(1, documents.size(), "单文档模式应只返回一个文档");

        Document doc = documents.get(0);

        // 验证文档元数据
        Assertions.assertNotNull(doc.getMetadata().get("title"), "文档标题不应为空");
        int pages = ((Number) doc.getMetadata().get("pages")).intValue();
        assertTrue(pages > 0, "页数应大于0");
        Assertions.assertNotNull(doc.getContent(), "文档内容不应为空");

        System.out.println("文档数: " + documents.size());
        System.out.println("标题: " + doc.getMetadata().get("title"));
        System.out.println("总页数: " + doc.getMetadata().get("pages"));
        System.out.println("内容预览: " + doc.getContent());
    }
}
