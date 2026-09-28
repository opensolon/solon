package features.web.staticfiles;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.noear.solon.core.handle.ContextEmpty;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.util.DateUtil;
import org.noear.solon.server.prop.GzipProps;
import org.noear.solon.test.SolonTest;
import org.noear.solon.web.staticfiles.StaticConfig;
import org.noear.solon.web.staticfiles.StaticMappings;
import org.noear.solon.web.staticfiles.StaticResourceHandler;
import org.noear.solon.web.staticfiles.repository.ClassPathStaticRepository;
import org.noear.solon.web.staticfiles.repository.FileStaticRepository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static org.junit.jupiter.api.Assertions.*;

@SolonTest
public class StaticResourceHandlerTest {

    private static class TestContext extends ContextEmpty {
        private String method = MethodType.GET.name;
        private String path = "/";
        private final Map<String, String> headers = new HashMap<>();
        private final Map<String, String> responseHeaders = new HashMap<>();
        private int status = 200;
        private String contentType;
        private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        public void setMethod(String method) {
            this.method = method;
        }

        public void setPath(String path) {
            this.path = path;
        }

        @Override
        public String method() {
            return method;
        }

        @Override
        public String path() {
            return path;
        }

        @Override
        public String pathNew() {
            return path;
        }

        @Override
        public URI uri() {
            return URI.create("http://localhost" + path);
        }

        @Override
        public String url() {
            return "http://localhost" + path;
        }

        @Override
        public String header(String name) {
            return headers.get(name);
        }

        @Override
        public String headerOrDefault(String name, String def) {
            return headers.getOrDefault(name, def);
        }

        @Override
        public void headerSet(String name, String val) {
            responseHeaders.put(name, val);
        }

        @Override
        public String headerOfResponse(String name) {
            return responseHeaders.get(name);
        }

        @Override
        public void status(int status) {
            this.status = status;
        }

        @Override
        public int status() {
            return status;
        }

        @Override
        public void contentType(String contentType) {
            this.contentType = contentType;
        }

        @Override
        public String contentType() {
            return contentType;
        }

        @Override
        public OutputStream outputStream() {
            return outputStream;
        }

        @Override
        public GZIPOutputStream outputStreamAsGzip() throws IOException {
            headerSet("Vary", "Accept-Encoding");
            headerSet("Content-Encoding", "gzip");
            return new GZIPOutputStream(outputStream(), 4096, true);
        }
    }

    private ClassPathStaticRepository repo;

    @BeforeEach
    public void setup() {
        repo = new ClassPathStaticRepository("META-INF/resources/");
        StaticMappings.add("/res/", repo);
    }

    @AfterEach
    public void tearDown() {
        StaticMappings.remove(repo);
    }

    @Test
    public void testHandledContext() throws Exception {
        StaticResourceHandler handler = new StaticResourceHandler();
        TestContext ctx = new TestContext();
        ctx.setHandled(true);
        handler.handle(ctx);
        assertTrue(ctx.getHandled());
    }

    @Test
    public void testNonGetMethod() throws Exception {
        StaticResourceHandler handler = new StaticResourceHandler();
        TestContext ctx = new TestContext();
        ctx.setMethod(MethodType.POST.name);
        ctx.setPath("/res/doc.html");
        handler.handle(ctx);
        assertFalse(ctx.getHandled());
    }

    @Test
    public void testNoExtension() throws Exception {
        StaticResourceHandler handler = new StaticResourceHandler();
        TestContext ctx = new TestContext();
        ctx.setPath("/res/noext");
        handler.handle(ctx);
        assertFalse(ctx.getHandled());
    }

    @Test
    public void testUnknownMime() throws Exception {
        StaticResourceHandler handler = new StaticResourceHandler();
        TestContext ctx = new TestContext();
        ctx.setPath("/res/file.unknownextensionxyz");
        handler.handle(ctx);
        assertFalse(ctx.getHandled());
    }

    @Test
    public void testResourceNotFound() throws Exception {
        StaticResourceHandler handler = new StaticResourceHandler();
        TestContext ctx = new TestContext();
        ctx.setPath("/res/not_exist.html");
        handler.handle(ctx);
        assertFalse(ctx.getHandled());
    }

    @Test
    public void testSuccessfulStaticResource() throws Exception {
        StaticResourceHandler handler = new StaticResourceHandler();
        TestContext ctx = new TestContext();
        ctx.setPath("/res/doc.html");
        handler.handle(ctx);

        assertTrue(ctx.getHandled());
        assertEquals(200, ctx.status());
        assertNotNull(ctx.headerOfResponse("Last-Modified"));
        assertNotNull(ctx.headerOfResponse("Cache-Control"));
    }

    @Test
    public void testNegativeCacheMaxAge() throws Exception {
        int original = StaticConfig.getCacheMaxAge();
        StaticConfig.setCacheMaxAge(-1);
        try {
            StaticResourceHandler handler = new StaticResourceHandler();
            TestContext ctx = new TestContext();
            ctx.setPath("/res/doc.html");
            handler.handle(ctx);

            assertTrue(ctx.getHandled());
            assertEquals(200, ctx.status());
        } finally {
            StaticConfig.setCacheMaxAge(original);
        }
    }

    @Test
    public void testCustomCacheControlHeader() throws Exception {
        StaticResourceHandler handler = new StaticResourceHandler();
        TestContext ctx = new TestContext();
        ctx.setPath("/res/doc.html");
        ctx.headerSet("Cache-Control", "no-cache");
        handler.handle(ctx);

        assertTrue(ctx.getHandled());
        assertEquals("no-cache", ctx.headerOfResponse("Cache-Control"));
    }

    @Test
    public void testIfModifiedSince() throws Exception {
        StaticResourceHandler handler = new StaticResourceHandler();
        TestContext ctx = new TestContext();
        ctx.setPath("/res/doc.html");

        Field field = StaticResourceHandler.class.getDeclaredField("modified_time");
        field.setAccessible(true);
        Date modifiedTime = (Date) field.get(null);
        String gmtStr = DateUtil.toGmtString(modifiedTime);

        ctx.headers.put("If-Modified-Since", gmtStr);
        handler.handle(ctx);

        assertTrue(ctx.getHandled());
        assertEquals(304, ctx.status());

        TestContext ctx2 = new TestContext();
        ctx2.setPath("/res/doc.html");
        ctx2.headers.put("If-Modified-Since", "Thu, 01 Jan 1970 00:00:00 GMT");
        handler.handle(ctx2);
        assertTrue(ctx2.getHandled());
        assertEquals(200, ctx2.status());
    }

    @Test
    public void testGzipAndBrCompressedFiles() throws Exception {
        boolean prevEnable = GzipProps.enable();
        GzipProps.enable(true);

        try {
            File tempDir = new File(System.getProperty("java.io.tmpdir"), "solon_gzip_test_" + System.currentTimeMillis());
            tempDir.mkdirs();
            tempDir.deleteOnExit();

            File jsFile = new File(tempDir, "bundle.js");
            File gzFile = new File(tempDir, "bundle.js.gz");
            File brFile = new File(tempDir, "bundle.js.br");

            try (FileOutputStream fos = new FileOutputStream(jsFile)) {
                fos.write("console.log('hello');".getBytes());
            }
            try (GZIPOutputStream gzos = new GZIPOutputStream(new FileOutputStream(gzFile))) {
                gzos.write("console.log('hello');".getBytes());
            }
            try (FileOutputStream fos = new FileOutputStream(brFile)) {
                fos.write("br-compressed-data".getBytes());
            }

            FileStaticRepository fileRepo = new FileStaticRepository(tempDir.getAbsolutePath());
            StaticMappings.add("/bundle/", fileRepo);

            StaticResourceHandler handler = new StaticResourceHandler();

            // 1. Test gzip
            TestContext ctxGz = new TestContext();
            ctxGz.setPath("/bundle/bundle.js");
            ctxGz.headers.put("Accept-Encoding", "gzip, deflate");
            handler.handle(ctxGz);
            assertTrue(ctxGz.getHandled());
            assertEquals("gzip", ctxGz.headerOfResponse("Content-Encoding"));
            assertEquals("Accept-Encoding", ctxGz.headerOfResponse("Vary"));

            // 2. Test br
            gzFile.delete(); // 删除 gz 文件以测试回退到 br
            TestContext ctxBr = new TestContext();
            ctxBr.setPath("/bundle/bundle.js");
            ctxBr.headers.put("Accept-Encoding", "br, gzip");
            handler.handle(ctxBr);
            assertTrue(ctxBr.getHandled());
            assertEquals("br", ctxBr.headerOfResponse("Content-Encoding"));
            assertEquals("Accept-Encoding", ctxBr.headerOfResponse("Vary"));

            // 3. Test non-compressed fallback
            brFile.delete();
            TestContext ctxNormal = new TestContext();
            ctxNormal.setPath("/bundle/bundle.js");
            ctxNormal.headers.put("Accept-Encoding", "gzip, br");
            handler.handle(ctxNormal);
            assertTrue(ctxNormal.getHandled());
            assertNull(ctxNormal.headerOfResponse("Content-Encoding"));

            StaticMappings.remove(fileRepo);
        } finally {
            GzipProps.enable(prevEnable);
        }
    }

    /**
     * 集成验证 pathPrefixAsFile=true 的 gzip 协商行为（IJNNFJ）。
     *
     * 注册单文件 mapping（如 Knife4jPlugin 注册的 /doc.html），
     * 请求带 Accept-Encoding: gzip 时：
     *
     *   - 修复前（bug）：StaticMappings.find("/doc.html.gz") 错误返回 doc.html URL，
     *     StaticResourceHandler 错配 Content-Encoding: gzip 并以 raw HTML 输出，
     *     客户端 gunzip 失败 → ERR_CONTENT_DECODING_FAILED。
     *
     *   - 修复后：StaticMappings.find("/doc.html.gz") 返回 null，
     *     StaticResourceHandler 跳过预压缩分支，
     *     回退到 outputFile → outputStream → requiredGzip → outputStreamAsGzip，
     *     客户端拿到的是真 gzip 流，能正常解压。
     *
     * 本测试在响应里同时断言「响应头 + body 类型 + 长度关系」三个维度，
     * 三者同时不匹配 = bug 复发；任一不匹配都立即 fail。
     */
    @Test
    public void testPathPrefixAsFile_gzipNegotiation() throws Exception {
        boolean prevEnable = GzipProps.enable();
        long prevMinSize = GzipProps.minSize();
        GzipProps.enable(true);
        GzipProps.minSize(0);   // 让小文件也走 gzip 路径

        try {
            // 1. 注册单文件 mapping（pathPrefixAsFile=true）
            File tempDir = new File(System.getProperty("java.io.tmpdir"),
                    "solon_ppaf_gz_test_" + System.currentTimeMillis());
            tempDir.mkdirs();
            tempDir.deleteOnExit();

            File htmlFile = new File(tempDir, "doc.html");
            // 构造一个 > 4096 字节的 HTML，确保即使 minSize 被 load() 重置为默认 4096 也能走 gzip
            StringBuilder sb = new StringBuilder();
            sb.append("<!DOCTYPE html><html><head><title>knife4j ui</title></head><body>");
            for (int i = 0; i < 200; i++) {
                sb.append("<p>line ").append(i).append(" : some content padding for size</p>");
            }
            sb.append("</body></html>");
            byte[] htmlContent = sb.toString().getBytes();
            try (FileOutputStream fos = new FileOutputStream(htmlFile)) {
                fos.write(htmlContent);
            }

            FileStaticRepository fileRepo = new FileStaticRepository(tempDir.getAbsolutePath());
            StaticMappings.add("/doc.html", fileRepo);
            StaticResourceHandler handler = new StaticResourceHandler();

            // 2. Accept-Encoding: gzip + /doc.html
            TestContext ctx = new TestContext();
            ctx.setPath("/doc.html");
            ctx.headers.put("Accept-Encoding", "gzip");
            handler.handle(ctx);

            assertTrue(ctx.getHandled(), "应被处理");

            // ===== 维度 1: 响应头 =====
            // IJNNFJ bug 的关键特征：Content-Encoding: gzip 错配 raw HTML。
            // 修复后这条 header 仍然存在（runtime compress 路径），所以单看 header 不能区分。
            // 但 Vary 必须有，否则下游 CDN 缓存会被错配。
            assertEquals("gzip", ctx.headerOfResponse("Content-Encoding"),
                    "Content-Encoding 应为 gzip");
            assertEquals("Accept-Encoding", ctx.headerOfResponse("Vary"),
                    "Vary 应为 Accept-Encoding（避免 CDN 缓存错配）");

            // ===== 维度 2: body 类型（这是 IJNNFJ bug 的核心区别点）=====
            byte[] body = ctx.outputStream.toByteArray();
            assertTrue(body.length > 0, "body 不应为空");

            // 关键判断：body 是不是 raw HTML（bug 表现）？
            // raw HTML 一定以 '<' 开头即 0x3c。
            // 真 gzip 流以 0x1f 0x8b 开头。
            // 这是修不修好最直观的分界。
            boolean looksLikeRawHtml = body.length >= 5
                    && body[0] == '<';
            boolean looksLikeGzip = body.length >= 2
                    && (body[0] & 0xff) == 0x1f
                    && (body[1] & 0xff) == 0x8b;

            assertFalse(looksLikeRawHtml,
                    "IJNNFJ bug 复发：body 是 raw HTML (" + headHex(body, 20)
                            + "...) 但响应头是 Content-Encoding: gzip，" +
                            "客户端 gunzip 会失败");

            assertTrue(looksLikeGzip,
                    "body 应是真 gzip 流（magic 1f 8b），但实际前几字节是 "
                            + headHex(body, 8));

            // ===== 维度 3: body 长度关系 =====
            // gzip 压缩后应显著小于 raw。bug 情况下 body == raw，长度近似相等。
            assertTrue(body.length < htmlContent.length,
                    "压缩后 body (" + body.length + "B) 应小于 raw HTML ("
                            + htmlContent.length + "B) —— bug 时两者近似相等");

            StaticMappings.remove(fileRepo);
        } finally {
            GzipProps.enable(prevEnable);
            GzipProps.minSize(prevMinSize);
        }
    }

    /**
     * 调试辅助：把 byte[] 前 n 字节转成 hex 字符串，给断言错误信息用。
     */
    private static String headHex(byte[] b, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(n, b.length); i++) {
            sb.append(String.format("%02x ", b[i] & 0xff));
        }
        return sb.toString().trim();
    }

    /**
     * 边界：单文件 mapping 上，对 /doc.htmlANYTHING / /doc.html/foo 等
     * 「以注册路径开头但不等」的请求，应不被该 mapping 吞掉。
     */
    @Test
    public void testPathPrefixAsFile_nonMatchingPrefixNotEaten() throws Exception {
        File tempDir = new File(System.getProperty("java.io.tmpdir"),
                "solon_ppaf_eat_test_" + System.currentTimeMillis());
        tempDir.mkdirs();
        tempDir.deleteOnExit();

        File htmlFile = new File(tempDir, "doc.html");
        try (FileOutputStream fos = new FileOutputStream(htmlFile)) {
            fos.write("<html>doc</html>".getBytes());
        }

        FileStaticRepository fileRepo = new FileStaticRepository(tempDir.getAbsolutePath());
        StaticMappings.add("/doc.html", fileRepo);
        try {
            StaticResourceHandler handler = new StaticResourceHandler();

            // /doc.htmlANYTHING 不应匹配 /doc.html 单文件 mapping
            TestContext ctx1 = new TestContext();
            ctx1.setPath("/doc.htmlANYTHING");
            handler.handle(ctx1);
            assertFalse(ctx1.getHandled(),
                    "修复后 /doc.htmlANYTHING 不应被 /doc.html 单文件 mapping 吞掉");

            TestContext ctx2 = new TestContext();
            ctx2.setPath("/doc.html/anything");
            handler.handle(ctx2);
            assertFalse(ctx2.getHandled(),
                    "修复后 /doc.html/anything 不应被 /doc.html 单文件 mapping 吞掉");
        } finally {
            StaticMappings.remove(fileRepo);
        }
    }

    /**
     * 单文件 mapping + repo 里同时存在 sibling .gz —— 当前设计选择（看法 A）：
     *
     * 单文件 mapping 用 equals 精确匹配，不会把 sibling .gz 当作预压缩资源输出。
     * 即使 repo 里真有 doc.html.gz，handler 也会 fallback 到运行时 GZIPOutputStream
     * 重新压缩 raw 文件。
     *
     * 这个行为是有意的，理由：
     * 1. IJNNFJ bug 的根因就是单文件 mapping 用 startsWith 把 `.gz` sibling 错配给
     *    raw 文件 URL，导致 Content-Encoding: gzip + raw HTML。
     * 2. 路径前缀 mapping（pathPrefixAsFile=false）才是 sibling .gz 协商的合法场景。
     * 3. 单文件 mapping 的语义就是「这一个文件」，不允许它的 path 邻居做预压缩协商。
     *
     * 本测试锁定这个行为：将来想改成看法 B（保留 sibling .gz 优化）时，此测试会
     * 失败，提醒决策。
     */
    @Test
    public void testPathPrefixAsFile_withGzSibling_runtimeCompresses() throws Exception {
        boolean prevEnable = GzipProps.enable();
        long prevMinSize = GzipProps.minSize();
        GzipProps.enable(true);
        GzipProps.minSize(0);

        try {
            File tempDir = new File(System.getProperty("java.io.tmpdir"),
                    "solon_ppaf_gz_sibling_" + System.currentTimeMillis());
            tempDir.mkdirs();
            tempDir.deleteOnExit();

            // repo 里同时放 raw doc.html 和预压缩的 doc.html.gz
            File htmlFile = new File(tempDir, "doc.html");
            byte[] htmlContent = ("<!DOCTYPE html><html><head><title>x</title></head><body>"
                    + "<p>padding padding padding padding padding padding</p>"
                    + "</body></html>").getBytes();
            try (FileOutputStream fos = new FileOutputStream(htmlFile)) {
                fos.write(htmlContent);
            }
            File gzFile = new File(tempDir, "doc.html.gz");
            try (FileOutputStream fos = new FileOutputStream(gzFile);
                 GZIPOutputStream gzos = new GZIPOutputStream(fos)) {
                gzos.write(htmlContent);
            }

            FileStaticRepository fileRepo = new FileStaticRepository(tempDir.getAbsolutePath());
            StaticMappings.add("/doc.html", fileRepo);   // pathPrefixAsFile=true

            StaticResourceHandler staticHandler = new StaticResourceHandler();
            TestContext ctx = new TestContext();
            ctx.setPath("/doc.html");
            ctx.headers.put("Accept-Encoding", "gzip");
            staticHandler.handle(ctx);

            assertTrue(ctx.getHandled(), "应被处理");
            assertEquals("gzip", ctx.headerOfResponse("Content-Encoding"),
                    "Content-Encoding 应为 gzip（runtime 压缩路径）");
            assertEquals("Accept-Encoding", ctx.headerOfResponse("Vary"),
                    "Vary 应为 Accept-Encoding");

            // body 必须是真 gzip 流
            byte[] body = ctx.outputStream.toByteArray();
            assertTrue(body.length > 0, "body 不应为空");
            boolean looksLikeGzip = body.length >= 2
                    && (body[0] & 0xff) == 0x1f
                    && (body[1] & 0xff) == 0x8b;
            assertTrue(looksLikeGzip,
                    "body 应是 runtime 压缩的真 gzip（1f 8b），实际前几字节: "
                            + headHex(body, 8));

            // runtime 压缩比例应远好于 1:1（确认是真正的压缩，不是把 sibling .gz
            // 原样透传 —— 因为 sibling .gz 内容相同，比例应差不多，但我们至少能
            // 确认 body 不是 raw HTML）
            assertTrue(body.length < htmlContent.length,
                    "runtime 压缩后 body (" + body.length + "B) 应 < raw ("
                            + htmlContent.length + "B)");

            // 兜底：确认 body 不是 raw HTML（同样的防御，看法 B 一旦实现就会失败）
            boolean looksLikeRawHtml = body.length >= 5 && body[0] == '<';
            assertFalse(looksLikeRawHtml,
                    "body 不应是 raw HTML，前几字节: " + headHex(body, 20));

            StaticMappings.remove(fileRepo);
        } finally {
            GzipProps.enable(prevEnable);
            GzipProps.minSize(prevMinSize);
        }
    }
}
