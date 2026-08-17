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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
}
