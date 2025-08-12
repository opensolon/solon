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
package org.noear.solon.boot.io;

import java.io.Closeable;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author noear
 * @since 1.6
 */
public class LimitedInputStream extends FilterInputStream implements Closeable {

    private final long sizeMax;
    private long count;

    public LimitedInputStream(final InputStream inputStream, final long limitSize) {
        super(inputStream);
        sizeMax = limitSize;
    }

    protected void raiseError(long pSizeMax, long pCount) throws IOException {
        throw new LimitedInputException("The input stream is too large: " + pSizeMax);
    }


    private void checkLimit() throws IOException {
        if (sizeMax > 0 && count > sizeMax) {
            raiseError(sizeMax, count);
        }
    }

    @Override
    public int read() throws IOException {
        final int res = super.read();
        if (res != -1) {
            count++;
            checkLimit();
        }
        return res;
    }

    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        final int res = super.read(b, off, len);
        if (res > 0) {
            count += res;
            checkLimit();
        }
        return res;
    }

    @Override
    public void close() throws IOException {
        super.close();
    }
}
