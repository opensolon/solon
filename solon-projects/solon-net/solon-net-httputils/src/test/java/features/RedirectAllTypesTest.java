package features;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.noear.solon.net.http.HttpResponse;
import org.noear.solon.net.http.HttpUtils;
import org.noear.solon.net.http.impl.jdk.JdkHttpUtilsFactory;
import org.noear.solon.net.http.impl.okhttp.OkHttpUtilsFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 针对 301/302/303/307/308 重定向行为的单元测试
 */
public class RedirectAllTypesTest {
    private static LocalRedirectServer server;

    @BeforeAll
    public static void setup() throws IOException {
        server = new LocalRedirectServer();
    }

    @AfterAll
    public static void teardown() {
        if (server != null) {
            server.close();
        }
    }

    private HttpUtils jdkHttp(String path) {
        return JdkHttpUtilsFactory.getInstance().http(server.url(path));
    }

    private HttpUtils okHttp(String path) {
        return OkHttpUtilsFactory.getInstance().http(server.url(path));
    }

    // ==================== GET 基础重定向测试 (301, 302, 303, 307, 308) ====================

    @Test
    public void testGetRedirect301() throws Exception {
        testAllHttpRedirectGet(301);
    }

    @Test
    public void testGetRedirect302() throws Exception {
        testAllHttpRedirectGet(302);
    }

    @Test
    public void testGetRedirect303() throws Exception {
        testAllHttpRedirectGet(303);
    }

    @Test
    public void testGetRedirect307() throws Exception {
        testAllHttpRedirectGet(307);
    }

    @Test
    public void testGetRedirect308() throws Exception {
        testAllHttpRedirectGet(308);
    }

    private void testAllHttpRedirectGet(int code) throws Exception {
        // JDK
        HttpResponse respJdk = jdkHttp("/redirect?code=" + code + "&target=/target-endpoint").exec("GET");
        Assertions.assertEquals(200, respJdk.code());
        Assertions.assertEquals("target:GET:body=[]", respJdk.bodyAsString());

        // OKHTTP
        HttpResponse respOk = okHttp("/redirect?code=" + code + "&target=/target-endpoint").exec("GET");
        Assertions.assertEquals(200, respOk.code());
        Assertions.assertEquals("target:GET:body=[]", respOk.bodyAsString());
    }

    // ==================== POST 降级为 GET (301, 302, 303) 测试 ====================

    @Test
    public void testPostRedirect301DowngradeToGet() throws Exception {
        testPostDowngrade(301);
    }

    @Test
    public void testPostRedirect302DowngradeToGet() throws Exception {
        testPostDowngrade(302);
    }

    @Test
    public void testPostRedirect303DowngradeToGet() throws Exception {
        testPostDowngrade(303);
    }

    private void testPostDowngrade(int code) throws Exception {
        // JDK
        HttpResponse respJdk = jdkHttp("/redirect?code=" + code + "&target=/target-endpoint")
                .body("hello=world", "application/x-www-form-urlencoded")
                .exec("POST");
        Assertions.assertEquals(200, respJdk.code());
        Assertions.assertEquals("target:GET:body=[]", respJdk.bodyAsString());

        // OKHTTP
        HttpResponse respOk = okHttp("/redirect?code=" + code + "&target=/target-endpoint")
                .body("hello=world", "application/x-www-form-urlencoded")
                .exec("POST");
        Assertions.assertEquals(200, respOk.code());
        Assertions.assertEquals("target:GET:body=[]", respOk.bodyAsString());
    }

    // ==================== POST 保持 POST 与 Body (307, 308) 测试 ====================

    @Test
    public void testPostRedirect307PreserveBody() throws Exception {
        testPostPreserveBody(307);
    }

    @Test
    public void testPostRedirect308PreserveBody() throws Exception {
        testPostPreserveBody(308);
    }

    private void testPostPreserveBody(int code) throws Exception {
        String payload = "{\"msg\":\"solon-redirect\"}";

        // 1. JDK - String body
        HttpResponse respJdkStr = jdkHttp("/redirect?code=" + code + "&target=/target-endpoint")
                .body(payload, "application/json")
                .exec("POST");
        Assertions.assertEquals(200, respJdkStr.code());
        Assertions.assertEquals("target:POST:body=[" + payload + "]", respJdkStr.bodyAsString());

        // 2. JDK - InputStream body (HttpStream rewind/reset)
        HttpResponse respJdkStream = jdkHttp("/redirect?code=" + code + "&target=/target-endpoint")
                .body(new ByteArrayInputStream(payload.getBytes(StandardCharsets.UTF_8)), "application/json")
                .exec("POST");
        Assertions.assertEquals(200, respJdkStream.code());
        Assertions.assertEquals("target:POST:body=[" + payload + "]", respJdkStream.bodyAsString());

        // 3. OkHttp - String body
        HttpResponse respOkStr = okHttp("/redirect?code=" + code + "&target=/target-endpoint")
                .body(payload, "application/json")
                .exec("POST");
        Assertions.assertEquals(200, respOkStr.code());
        Assertions.assertEquals("target:POST:body=[" + payload + "]", respOkStr.bodyAsString());

        // 4. OkHttp - InputStream body
        HttpResponse respOkStream = okHttp("/redirect?code=" + code + "&target=/target-endpoint")
                .body(new ByteArrayInputStream(payload.getBytes(StandardCharsets.UTF_8)), "application/json")
                .exec("POST");
        Assertions.assertEquals(200, respOkStream.code());
        Assertions.assertEquals("target:POST:body=[" + payload + "]", respOkStream.bodyAsString());
    }

    // ==================== 异步请求 (execAsync) 30x 重定向测试 ====================

    @Test
    public void testAsyncRedirect307() throws Exception {
        String payload = "{\"async\":true}";

        // JDK 异步 307
        HttpResponse respJdk = jdkHttp("/redirect?code=307&target=/target-endpoint")
                .body(payload, "application/json")
                .execAsync("POST")
                .get();
        Assertions.assertEquals(200, respJdk.code());
        Assertions.assertEquals("target:POST:body=[" + payload + "]", respJdk.bodyAsString());

        // OkHttp 异步 307
        HttpResponse respOk = okHttp("/redirect?code=307&target=/target-endpoint")
                .body(payload, "application/json")
                .execAsync("POST")
                .get();
        Assertions.assertEquals(200, respOk.code());
        Assertions.assertEquals("target:POST:body=[" + payload + "]", respOk.bodyAsString());
    }

    @Test
    public void testAsyncRedirect302DowngradeToGet() throws Exception {
        // JDK 异步 302
        HttpResponse respJdk = jdkHttp("/redirect?code=302&target=/target-endpoint")
                .body("hello=async", "application/x-www-form-urlencoded")
                .execAsync("POST")
                .get();
        Assertions.assertEquals(200, respJdk.code());
        Assertions.assertEquals("target:GET:body=[]", respJdk.bodyAsString());

        // OkHttp 异步 302
        HttpResponse respOk = okHttp("/redirect?code=302&target=/target-endpoint")
                .body("hello=async", "application/x-www-form-urlencoded")
                .execAsync("POST")
                .get();
        Assertions.assertEquals(200, respOk.code());
        Assertions.assertEquals("target:GET:body=[]", respOk.bodyAsString());
    }

    // ==================== 循环重定向异常测试 ====================

    @Test
    public void testRedirectLoopExceedsLimit() {
        // JDK
        Assertions.assertThrows(Exception.class, () -> {
            jdkHttp("/loop-redirect").get();
        });

        // OkHttp
        Assertions.assertThrows(Exception.class, () -> {
            okHttp("/loop-redirect").get();
        });
    }

    // ==================== 绝对与相对路径重定向测试 ====================

    @Test
    public void testAbsoluteLocationUrlRedirect() throws Exception {
        String absoluteTarget = server.url("/target-endpoint");

        // JDK
        HttpResponse respJdk = jdkHttp("/redirect?code=302&target=" + absoluteTarget).exec("GET");
        Assertions.assertEquals(200, respJdk.code());
        Assertions.assertEquals("target:GET:body=[]", respJdk.bodyAsString());

        // OkHttp
        HttpResponse respOk = okHttp("/redirect?code=302&target=" + absoluteTarget).exec("GET");
        Assertions.assertEquals(200, respOk.code());
        Assertions.assertEquals("target:GET:body=[]", respOk.bodyAsString());
    }
}
