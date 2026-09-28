package org.noear.solon.web.staticfiles;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.noear.solon.test.SolonTest;
import org.noear.solon.web.staticfiles.repository.ClassPathStaticRepository;
import org.noear.solon.web.staticfiles.repository.FileStaticRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.zip.GZIPOutputStream;

import static org.junit.jupiter.api.Assertions.*;

@SolonTest
public class StaticMappingsTest {

    private StaticRepository repo1;
    private StaticRepository repo2;
    private StaticRepository repoRoot;
    private StaticRepository fileRepo;
    private StaticRepository gzFileRepo;

    /**
     * 每个测试都新建独立的临时目录、写入 doc.html.gz、跑完删除 —— 不在
     * src/test/resources/ 下放静态 fixture，避免污染 classpath 资源树。
     * 单测很轻，生成/清理成本可忽略。
     */
    private File gzTempDir;

    @BeforeEach
    public void setup() throws Exception {
        repo1 = new ClassPathStaticRepository("META-INF/resources/");
        repo2 = new ClassPathStaticRepository("META-INF/resources/webjars/");
        repoRoot = new ClassPathStaticRepository("");
        fileRepo = new ClassPathStaticRepository("META-INF/resources/");

        gzTempDir = new File(System.getProperty("java.io.tmpdir"),
                "solon_mappings_gz_" + System.currentTimeMillis());
        gzTempDir.mkdirs();

        File gz = new File(gzTempDir, "doc.html.gz");
        try (FileOutputStream fos = new FileOutputStream(gz);
             GZIPOutputStream gzos = new GZIPOutputStream(fos)) {
            gzos.write("<html>gzip fixture</html>".getBytes());
        }

        gzFileRepo = new FileStaticRepository(gzTempDir.getAbsolutePath());

        StaticMappings.locationMap.clear();
    }

    @AfterEach
    public void tearDown() {
        StaticMappings.locationMap.clear();
        if (gzTempDir != null && gzTempDir.exists()) {
            deleteRecursive(gzTempDir);
        }
    }

    private static void deleteRecursive(File f) {
        if (f.isDirectory()) {
            File[] children = f.listFiles();
            if (children != null) {
                for (File c : children) {
                    deleteRecursive(c);
                }
            }
        }
        f.delete();
    }

    @Test
    public void testMappings() throws Exception {
        assertEquals(0, StaticMappings.count());

        StaticMappings.add("/res/", repo1);
        assertEquals(1, StaticMappings.count());

        StaticMappings.add("webjars/", repo2);
        assertEquals(2, StaticMappings.count());

        // repositoryIncPrefix = true
        StaticMappings.addDo("/META-INF/resources/", repoRoot, true);

        // pathPrefixAsFile = true
        StaticMappings.addDo("/doc.html", fileRepo, false);

        URL url1 = StaticMappings.find("/res/doc.html");
        assertNotNull(url1);

        URL url2 = StaticMappings.find("/webjars/hello.html");
        assertNotNull(url2);

        URL url3 = StaticMappings.find("/doc.html");
        assertNotNull(url3);

        URL urlInc = StaticMappings.find("/META-INF/resources/doc.html");
        assertNotNull(urlInc);

        URL url4 = StaticMappings.find("/not_found.html");
        assertNull(url4);

        assertNull(StaticMappings.find("/res/../doc.html"));
        assertNull(StaticMappings.find("/res/..\\doc.html"));
        assertNull(StaticMappings.find("/res/../../doc.html"));
        assertNull(StaticMappings.find("/res/..\\..\\doc.html"));
        assertNull(StaticMappings.find("/res/sub/../../doc.html"));

        StaticMappings.remove(repo1);
        assertNull(StaticMappings.find("/res/doc.html"));

        new StaticMappings();
    }

    /**
     * 单文件映射（pathPrefixAsFile=true）应只匹配精确路径，
     * 不得用 startsWith 把 /doc.html.gz /doc.htmlANYTHING /doc.html/anything 等
     * 误匹配为 /doc.html。详见 IJNNFJ。
     */
    @Test
    public void testPathPrefixAsFile_exactMatch() throws Exception {
        StaticMappings.addDo("/doc.html", fileRepo, false);

        // 精确匹配：返回注册的文件 URL
        assertNotNull(StaticMappings.find("/doc.html"));

        // 编码协商后缀（.gz / .br）不应匹配单文件 mapping
        // 这是 StaticResourceHandler 错配 Content-Encoding 的根因
        assertNull(StaticMappings.find("/doc.html.gz"));
        assertNull(StaticMappings.find("/doc.html.br"));

        // 任何以注册路径开头的请求都不应匹配
        assertNull(StaticMappings.find("/doc.htmlANYTHING"));
        assertNull(StaticMappings.find("/doc.html_bak"));
        assertNull(StaticMappings.find("/doc.html/anything"));
        assertNull(StaticMappings.find("/doc.html/"));
    }

    /**
     * 路径前缀映射（pathPrefixAsFile=false）行为保持不变：
     * 任何以 pathPrefix 开头的请求都进入匹配。
     *
     * .gz 兄弟文件由 setup() 动态生成在 gzTempDir 下，测试通过
     * FileStaticRepository(gzTempDir) 访问 —— 不污染 classpath。
     */
    @Test
    public void testPathPrefix_notAsFile_unchanged() throws Exception {
        StaticMappings.add("/res/", gzFileRepo);
        StaticMappings.add("/webjars/", repo2);

        // 子路径匹配（gzFileRepo 含 doc.html.gz，repo2 含 webjars/hello.html）
        assertNotNull(StaticMappings.find("/res/doc.html.gz"));
        assertNotNull(StaticMappings.find("/webjars/hello.html"));

        // 兄弟路径不匹配
        assertNull(StaticMappings.find("/other/doc.html"));
    }

    /**
     * 端到端验证 .gz / .br 后缀被注册成 pathPrefixAsFile=true 时的 find() 行为。
     *
     * 与 StaticLocationTest#testStaticLocation 配对：
     * - 构造器契约：构造器接受 `/doc.html.gz` + pathPrefixAsFile=true
     *   （loc3 / loc4）
     * - find() 契约（本测试）：注册后只有精确匹配命中，不会被 startsWith 吞
     *
     * 这是"有人不小心把 `.gz`/`.br` 当单文件注册"场景的端到端兜底。
     */
    @Test
    public void testPathPrefixAsFile_dotSuffixes_findBehavior() throws Exception {
        // gzFileRepo 是动态生成的临时目录（含 doc.html.gz）
        StaticMappings.addDo("/doc.html.gz", gzFileRepo, false);

        // 精确匹配命中
        assertNotNull(StaticMappings.find("/doc.html.gz"),
                "注册的 /doc.html.gz 单文件 mapping 应能被精确匹配");

        // 任何以注册路径开头的请求都不应匹配（IJNNFJ fix 后行为）
        assertNull(StaticMappings.find("/doc.html.gzANYTHING"));
        assertNull(StaticMappings.find("/doc.html.gz_bak"));
        assertNull(StaticMappings.find("/doc.html.gz/anything"));
        assertNull(StaticMappings.find("/doc.html.gz/"));

        // 不同后缀也不应匹配
        assertNull(StaticMappings.find("/doc.html.br"));
        assertNull(StaticMappings.find("/doc.html"));
    }
}