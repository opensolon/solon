package graphql.solon.resource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author fuzi1996
 * @since 2.3
 */
public class ByteArrayResource implements Resource {

    private final byte[] byteArray;

    public ByteArrayResource(byte[] byteArray) {
        this.byteArray = byteArray;
    }

    public ByteArrayResource(String content, Charset charset) {
        this.byteArray = content.getBytes(charset);
    }

    public ByteArrayResource(String content) {
        this.byteArray = content.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(this.byteArray);
    }
}
