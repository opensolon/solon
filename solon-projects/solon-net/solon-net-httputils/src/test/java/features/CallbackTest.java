package features;

import org.junit.jupiter.api.Test;
import org.noear.solon.net.http.HttpResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author noear 2025/3/29 created
 */
public class CallbackTest {
    @Test
    public void case1() {
        AtomicInteger counter = new AtomicInteger(0);
        CompletableFuture<HttpResponse> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException());
        future.whenComplete((resp, err) -> {
            if (err != null) {
                System.out.println("ERR");
                counter.set(1);
            }
        });

        assert counter.get() == 1;
    }
}
