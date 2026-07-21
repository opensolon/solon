package features;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.noear.solon.net.http.textstream.ServerSentEvent;

import java.time.Duration;
import java.util.HashMap;

public class ServerSentEventBuilderTest {

    @Test
    void testBuilderDataNotTrimmed() {
        ServerSentEvent sse = ServerSentEvent.builder().data("  hello  ").build();
        Assertions.assertEquals("  hello  ", sse.getData());
    }

    @Test
    void testBuilderEventIdRetry() {
        ServerSentEvent sse = ServerSentEvent.builder()
                .id("1")
                .event("update")
                .retry("5000")
                .data("payload")
                .build();
        Assertions.assertEquals("1", sse.getId());
        Assertions.assertEquals("update", sse.getEvent());
        Assertions.assertEquals("5000", sse.getRetry());
        Assertions.assertEquals("payload", sse.getData());
    }

    @Test
    void testBuilderRetryDurationNull() {
        ServerSentEvent sse = ServerSentEvent.builder().retry((Duration) null).build();
        Assertions.assertNull(sse.getRetry());
    }

    @Test
    void testBuilderRetryDuration() {
        ServerSentEvent sse = ServerSentEvent.builder().retry(Duration.ofMillis(3000)).build();
        Assertions.assertEquals("3000", sse.getRetry());
    }

    @Test
    void testBuilderBuildReturnsNewInstance() {
        ServerSentEvent sse1 = ServerSentEvent.builder().data("a").build();
        ServerSentEvent sse2 = ServerSentEvent.builder().data("a").build();
        Assertions.assertNotSame(sse1, sse2);
    }

    @Test
    void testConstructorTrimsData() {
        ServerSentEvent sse = new ServerSentEvent(new HashMap<>(), "  hello  ");
        Assertions.assertEquals("hello", sse.getData());
    }

    @Test
    void testBuilderComment() {
        ServerSentEvent sse = ServerSentEvent.builder().comment("test comment").build();
        Assertions.assertEquals("test comment", sse.getComment());
    }
}