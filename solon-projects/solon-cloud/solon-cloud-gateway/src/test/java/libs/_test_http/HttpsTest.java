package libs._test_http;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.client.WebClient;

/**
 * @author noear 2024/9/3 created
 */
public class HttpsTest {
    public static void main(String[] args) {
        WebClient.create(Vertx.vertx()).requestAbs(HttpMethod.GET, "https://h5.noear.org")
                .send(ar -> {
                    if (ar.succeeded()) {
                        System.out.println(ar.result().bodyAsString());
                    } else {
                        System.out.println(ar.cause().getMessage());
                    }
                });
    }
}
