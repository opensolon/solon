package org.noear.solon.boot.util;

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
        throw new IOException("The stream is too large: " + pSizeMax);
    }


    private void checkLimit() throws IOException {
        if (count > sizeMax) {
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
