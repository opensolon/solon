package features;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.noear.solon.Solon;
import org.noear.solon.net.http.HttpConfiguration;
import org.noear.solon.net.http.impl.jdk.JdkHttpUtilsFactory;
import org.noear.solon.net.http.impl.okhttp.OkHttpUtilsFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * 默认 User-Agent 测试（JDK 与 OkHttp 双实现）
 *
 * @since 4.0.5
 */
public class UserAgentTest {
    private LocalHttpServer server;
    private String echoUrl;

    @BeforeEach
    public void setup() throws Exception {
        server = new LocalHttpServer();
        echoUrl = server.url("/echo-header?User-Agent");
        HttpConfiguration.setUserAgent("solon-httputils/" + Solon.version());
    }

    @AfterEach
    public void teardown() throws Exception {
        server.close();
        HttpConfiguration.setUserAgent("solon-httputils/" + Solon.version());
    }

    @Test
    public void defaultUserAgentJdk() throws Exception {
        String ua = JdkHttpUtilsFactory.getInstance().http(echoUrl).get();
        assertEquals("solon-httputils/" + Solon.version(), ua);
    }

    @Test
    public void defaultUserAgentOkhttp() throws Exception {
        String ua = OkHttpUtilsFactory.getInstance().http(echoUrl).get();
        assertEquals("solon-httputils/" + Solon.version(), ua);
    }

    @Test
    public void explicitUserAgentOverrides() throws Exception {
        String ua = JdkHttpUtilsFactory.getInstance().http(echoUrl).userAgent("my-ua").get();
        assertEquals("my-ua", ua);
    }

    @Test
    public void globalUserAgentApplies() throws Exception {
        HttpConfiguration.setUserAgent("global-ua");
        String ua = JdkHttpUtilsFactory.getInstance().http(echoUrl).get();
        assertEquals("global-ua", ua);
    }

    @Test
    public void disabledUserAgentFallsBack() throws Exception {
        HttpConfiguration.setUserAgent(null);
        String ua = JdkHttpUtilsFactory.getInstance().http(echoUrl).get();
        // 禁用后回退到底层实现（JDK 发送 Java/x，OkHttp 发送 okhttp/x）
        assertNotNull(ua);
        assertFalse(ua.startsWith("solon-httputils"));
    }
}
