/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.web.vertx;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * 响应输出流
 *
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
            byte[] bytes = new byte[buf.position()];

            buf.flip();
            buf.get(bytes);

            response.write(Buffer.buffer(bytes));
            buf.clear();
        }
    }

    @Override
    public void flush() throws IOException {
        if (buf.position() > 0) {
            //有内容，刷一次
            byte[] bytes = new byte[buf.position()];

            buf.flip();
            buf.get(bytes);

            response.write(Buffer.buffer(bytes));
            buf.clear();
        }
    }
}
