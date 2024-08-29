/*
 * Copyright 2017-2024 noear.org and authors
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
package org.noear.solon.boot.vertx;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author noear
 * @since 2.9
 */
public class RequestInputStream extends InputStream {
    private final Buffer buffer;
    private int index;
    private boolean isEnd;

    public RequestInputStream(HttpServerRequest request) {
        this.buffer = Buffer.buffer();
        this.index = 0;

        request.handler(buffer -> {
            this.add(buffer);
        });
        request.end(ar -> this.end());
    }

    private void add(Buffer buf) {
       this.buffer.appendBuffer(buf);
    }

    private void end() {
        isEnd = true;
    }

    @Override
    public int read() throws IOException {
        while (true) {
            if (index < buffer.length()) {
                return buffer.getByte(index);
            }

            if (isEnd) {
                return -1;
            }
        }
    }
}
