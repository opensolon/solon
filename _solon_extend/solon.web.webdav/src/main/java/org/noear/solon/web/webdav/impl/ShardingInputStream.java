package org.noear.solon.web.webdav.impl;

import java.io.IOException;
import java.io.InputStream;

/**
 * 分片文件流
 * @author 阿范
 */
public class ShardingInputStream extends InputStream {
    private InputStream in;
    private long length;
    private long hasLength;

    public ShardingInputStream(InputStream in, long start, long length) {
        this.in = in;
        this.length = length;
        this.hasLength = length;
        if (start > 0) {
            long at = start;
            while (at > 0) {
                try {
                    long amt = this.in.skip(at);
                    at -= amt;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public int read() throws IOException {
        if (this.hasLength <= 0) {
            return -1;
        }
        int r = in.read();
        this.hasLength -= 1;
        return r;
    }

    @Override
    public int read(byte b[]) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public long skip(long n) throws IOException {
        throw new IOException("此流不支持跳过");
    }

    @Override
    public int read(byte b[], int off, int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        } else if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return 0;
        }
        if (this.hasLength <= 0) {
            return -1;
        }
        if (this.hasLength < len) {
            len = (int) this.hasLength;
        }
        int c = in.read(b, off, len);
        this.hasLength -= c;
        return c;
    }

    @Override
    public int available() {
        return (int) this.length;
    }

    @Override
    public void close() {
        try{
            this.in.close();
        }catch (Exception e){

        }
    }

    @Override
    public synchronized void mark(int readlimit) {
        throw new RuntimeException("此流不支持标记");
    }

    @Override
    public synchronized void reset() {
        throw new RuntimeException("此流不支持重置");
    }
}
