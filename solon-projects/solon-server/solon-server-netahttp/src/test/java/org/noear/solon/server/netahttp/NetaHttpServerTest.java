package org.noear.solon.server.netahttp;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonTest;

/**
 * NetaHttp 服务器集成测试
 *
 * @author noear
 * @since 3.10
 */
@SolonTest(NetaHttpServerTest.App.class)
public class NetaHttpServerTest extends HttpTester {

    @Controller
    public static class App {
        public static void main(String[] args) {
            Solon.start(App.class, args);
        }

        @Mapping("hello")
        public String hello() {
            return "hello";
        }

        @Mapping("echo")
        public String echo(String name) {
            return "echo:" + name;
        }
    }

    @AfterAll
    public static void aftAll() {
        if (Solon.app() != null) {
            Solon.stopBlock();
        }
    }

    @Test
    public void testHttpGet() throws Exception {
        String result = path("/hello").get();
        assert "hello".equals(result) : "Expected 'hello' but got: " + result;
    }

    @Test
    public void testHttpPost() throws Exception {
        String result = path("/echo").data("name", "world").post();
        assert result != null && result.contains("world") : "Expected 'world' in response but got: " + result;
    }

    @Test
    public void testNotFound() throws Exception {
        try {
            String result = path("/not_exist").get();
            // 如果能返回内容，404通常body为空或有错误信息
            assert result == null || result.length() == 0 || result.contains("404") : "Should be 404";
        } catch (Exception e) {
            // 连接可能被直接关闭，这也是可接受的
        }
    }
}
