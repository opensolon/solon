package features;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import org.noear.solon.net.http.impl.HttpStream;

import java.io.ByteArrayInputStream;
import java.io.BufferedInputStream;
import java.io.File;
import java.nio.file.Files;

/**
 * Tests for {@link HttpStream#getContentLength()} behavior across
 * different input source types (ByteArrayInputStream, empty stream,
 * wrapper streams, and file-backed streams).
 */
public class HttpStreamContentLengthTest {

    @Test
    void testByteArrayInputStreamWithContent() throws Exception {
        HttpStream stream = new HttpStream(
                new ByteArrayInputStream("hello world".getBytes()), null);
        Assertions.assertEquals(11, stream.getContentLength());
    }

    @Test
    void testByteArrayInputStreamEmpty() throws Exception {
        HttpStream stream = new HttpStream(
                new ByteArrayInputStream(new byte[0]), null);
        Assertions.assertEquals(-1, stream.getContentLength());
    }

    @Test
    void testNonByteArrayInputStream() throws Exception {
        HttpStream stream = new HttpStream(
                new BufferedInputStream(new ByteArrayInputStream("test".getBytes())), null);
        Assertions.assertEquals(-1, stream.getContentLength());
    }

    @Test
    void testFile() throws Exception {
        File tempFile = Files.createTempFile("httpstream-test", ".txt").toFile();
        try {
            byte[] data = "file content".getBytes();
            Files.write(tempFile.toPath(), data);

            HttpStream stream = new HttpStream(tempFile.getName(), tempFile);
            Assertions.assertEquals(tempFile.length(), stream.getContentLength());
        } finally {
            Files.deleteIfExists(tempFile.toPath());
        }
    }
}