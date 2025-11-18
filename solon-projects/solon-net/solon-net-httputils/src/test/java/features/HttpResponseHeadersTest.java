package features;

import org.junit.jupiter.api.Test;
import org.noear.solon.net.http.HttpResponse;
import org.noear.solon.net.http.HttpUtils;

import java.io.IOException;
import java.util.*;

public class HttpResponseHeadersTest {

    @Test
    public void testHeaderMap() {
        try (HttpResponse resp = HttpUtils.http("http://localhost:8080/hello")
                .data("name", "world")
                .exec("POST")) {

            // 方法1：传统方式获取headers
            System.out.println("【传统方式】通过 headerNames() + headers(headerName) 获取：");
            Map<String, List<String>> headers = new HashMap<>();
            Collection<String> headerNames = resp.headerNames();
            for (String headerName : headerNames) {
                headers.put(headerName, resp.headers(headerName));
            }
            headers.forEach((k, v) -> System.out.println("  " + k + " -> " + v));

            System.out.println("----------------------------------------");

            // 方法2：使用新增的headers()接口
            System.out.println("【新增方式】直接通过 headers() 获取：");
            Map<String, List<String>> all = resp.headerMap();
            all.forEach((k, v) -> System.out.println("  " + k + " -> " + v));

        } catch (IOException e) {
            System.err.println("HTTP请求失败：" + e.getMessage());
            e.printStackTrace();
        }
    }
}