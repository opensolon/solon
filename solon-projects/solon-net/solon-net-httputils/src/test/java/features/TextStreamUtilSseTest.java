package features;

import org.junit.jupiter.api.Test;
import org.noear.solon.net.http.textstream.ServerSentEvent;
import org.noear.solon.net.http.textstream.TextStreamUtil;
import reactor.test.StepVerifier;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class TextStreamUtilSseTest {

    @Test
    void testDoneNoTrailingSpace() {
        String input = "data: [DONE]\n\n";
        StepVerifier.create(TextStreamUtil.parseSseStream(
                        new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8))))
                .expectNextMatches(event -> "[DONE]".equals(event.getData()))
                .verifyComplete();
    }

    @Test
    void testDoneWithTrailingSpace() {
        String input = "data: [DONE] \n\n";
        StepVerifier.create(TextStreamUtil.parseSseStream(
                        new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8))))
                .expectNextMatches(event -> "[DONE]".equals(event.getData()))
                .verifyComplete();
    }

    @Test
    void testMultilineData() {
        String input = "data: line1\ndata: line2\n\n";
        StepVerifier.create(TextStreamUtil.parseSseStream(
                        new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8))))
                .expectNextMatches(event -> "line1\nline2".equals(event.getData()))
                .verifyComplete();
    }

    @Test
    void testCommentLineIgnored() {
        String input = ": this is a comment\ndata: hello\n\n";
        StepVerifier.create(TextStreamUtil.parseSseStream(
                        new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8))))
                .expectNextMatches(event -> "hello".equals(event.getData()))
                .verifyComplete();
    }

    @Test
    void testEventAndIdFields() {
        String input = "event: update\nid: 42\ndata: payload\n\n";
        StepVerifier.create(TextStreamUtil.parseSseStream(
                        new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8))))
                .expectNextMatches(event -> "update".equals(event.getEvent())
                        && "42".equals(event.getId())
                        && "payload".equals(event.getData()))
                .verifyComplete();
    }

    @Test
    void testRetryField() {
        String input = "retry: 5000\ndata: test\n\n";
        StepVerifier.create(TextStreamUtil.parseSseStream(
                        new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8))))
                .expectNextMatches(event -> "5000".equals(event.getRetry())
                        && "test".equals(event.getData()))
                .verifyComplete();
    }
}