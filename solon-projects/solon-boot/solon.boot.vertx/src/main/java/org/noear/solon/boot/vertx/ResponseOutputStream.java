package org.noear.solon.boot.vertx;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * @author noear
 * @since 2.9
 */
public class ResponseOutputStream extends OutputStream {
    private ByteBuffer buf;
    private HttpServerResponse response;

    public ResponseOutputStream(HttpServerResponse response, int bufSize) {
        this.response = response;
        this.buf = ByteBuffer.allocate(bufSize);
    }

    @Override
    public void write(int b) throws IOException {
        buf.put((byte) b);

        if (buf.hasRemaining() == false) {
            //没空间了，刷一次
            response.write(Buffer.buffer(buf.array()));
            buf.clear();
        }
    }

    @Override
    public void flush() throws IOException {
        if (buf.hasRemaining()) {
            //有内容，刷一次
            response.write(Buffer.buffer(buf.array()));
            buf.clear();
        }
    }
}
