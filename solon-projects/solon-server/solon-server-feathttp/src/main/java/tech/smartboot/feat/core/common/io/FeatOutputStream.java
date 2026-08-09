/*
 *  Copyright (C) [2024] smartboot [zhengjunweimail@163.com]
 *
 *  企业用户未经smartboot组织特别许可，需遵循Apache-2.0开源协议合理合法使用本项目。
 *
 *   Enterprise users are required to use this project reasonably
 *   and legally in accordance with the Apache-2.0 open source agreement
 *  without special permission from the smartboot organization.
 */

package tech.smartboot.feat.core.common.io;

import io.github.smartboot.socket.transport.WriteBuffer;
import tech.smartboot.feat.core.common.FeatUtils;
import tech.smartboot.feat.core.common.Reset;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
public abstract class FeatOutputStream extends OutputStream implements Reset {
    protected final WriteBuffer writeBuffer;
    protected boolean committed = false;
    protected boolean chunkedSupport = true;
    /**
     * 当前流是否完结
     */
    protected boolean closed = false;
    protected long remaining = -1;
    private Supplier<Map<String, String>> trailerSupplier;
    private WriteListener writeListener;

    public FeatOutputStream(WriteBuffer writeBuffer) {
        this.writeBuffer = writeBuffer;
    }

    /**
     * 不推荐使用该方法，此方法性能不佳
     *
     * @param b the <code>byte</code>.
     * @throws IOException
     */
    @Override
    public final void write(int b) throws IOException {
        byte[] bytes = new byte[1];
        bytes[0] = (byte) b;
        write(bytes);
    }

    /**
     * 输出Http响应
     *
     * @param b
     * @param off
     * @param len
     * @throws IOException
     */
    public void write(byte[] b, int off, int len) throws IOException {
        writeHeader(HeaderWriteSource.WRITE);

        if (len == 0) {
            return;
        }

        if (chunkedSupport) {
            byte[] start = (Integer.toHexString(len) + "\r\n").getBytes();
            writeBuffer.write(start);
            writeBuffer.write(b, off, len);
            writeBuffer.write(FeatUtils.CRLF_BYTES);
        } else {
            if (remaining >= 0) {
                remaining -= len;
                if (remaining < 0) {
                    throw new IOException("");
                }
            }
            writeBuffer.write(b, off, len);
        }
    }

    public final void write(byte[] b, int off, int len, Consumer<FeatOutputStream> consumer) throws IOException {
        writeHeader(HeaderWriteSource.WRITE);
        if (len == 0) {
            consumer.accept(this);
            return;
        }
        if (chunkedSupport) {
            byte[] start = (Integer.toHexString(len) + "\r\n").getBytes();
            writeBuffer.write(start);
            writeBuffer.write(b, off, len);
            writeBuffer.write(FeatUtils.CRLF_BYTES, 0, 2, writeBuffer -> consumer.accept(FeatOutputStream.this));
        } else {
            writeBuffer.write(b, off, len, writeBuffer -> consumer.accept(FeatOutputStream.this));
        }
    }

    public final void transferFrom(ByteBuffer buffer, Consumer<FeatOutputStream> consumer) throws IOException {
        writeHeader(HeaderWriteSource.WRITE);
        if (!chunkedSupport) {
            writeBuffer.transferFrom(buffer, writeBuffer -> consumer.accept(FeatOutputStream.this));
            return;
        }
        byte[] start = (Integer.toHexString(buffer.remaining()) + "\r\n").getBytes();
        if (buffer.position() >= start.length) {
            buffer.put(start, buffer.position() - start.length, start.length);
            buffer.position(buffer.position() - start.length);
        } else {
            writeBuffer.write(start);
        }
        if (buffer.capacity() - buffer.limit() >= FeatUtils.CRLF_BYTES.length) {
            buffer.put(FeatUtils.CRLF_BYTES, buffer.limit(), FeatUtils.CRLF_BYTES.length);
            buffer.limit(buffer.limit() + FeatUtils.CRLF_BYTES.length);
            writeBuffer.transferFrom(buffer, writeBuffer -> consumer.accept(FeatOutputStream.this));
        } else {
            writeBuffer.transferFrom(buffer, writeBuffer -> {
                try {
                    writeBuffer.write(FeatUtils.CRLF_BYTES, 0, 2, buffer1 -> consumer.accept(FeatOutputStream.this));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }


    @Override
    public final void flush() throws IOException {
        writeHeader(HeaderWriteSource.FLUSH);
        writeBuffer.flush();
    }

    @Override
    public final void close() throws IOException {
        if (closed) {
            throw new IOException("outputStream has already closed");
        }
        writeHeader(HeaderWriteSource.CLOSE);

        if (chunkedSupport) {
            if (trailerSupplier != null) {
                writeBuffer.write("0\r\n".getBytes());
                Map<String, String> map = trailerSupplier.get();
                for (String key : map.keySet()) {
                    writeBuffer.write((key + ":" + map.get(key) + "\r\n").getBytes());
                }
                writeBuffer.write(FeatUtils.CRLF_BYTES);
            } else {
                writeBuffer.write(FeatUtils.CHUNKED_END_BYTES);
            }
        }
        closed = true;
    }

    protected final byte[] getBytes(String str) {
        return str.getBytes(StandardCharsets.US_ASCII);
    }

    public final boolean isClosed() {
        return closed;
    }

    public final void reset() {
        committed = closed = false;
        chunkedSupport = true;
    }


    protected abstract void writeHeader(HeaderWriteSource source) throws IOException;

    public void disableChunked() {
        this.chunkedSupport = false;
    }

    public boolean isCommitted() {
        return committed;
    }

    public boolean isChunkedSupport() {
        return chunkedSupport;
    }

    public Supplier<Map<String, String>> getTrailerFields() {
        return trailerSupplier;
    }

    public void setTrailerFields(Supplier<Map<String, String>> supplier) {
        this.trailerSupplier = supplier;
    }

    protected void writeLongString(long value) throws IOException {
        if (value < 0) {
            throw new IllegalArgumentException("");
        } else if (value < 10) {
            writeBuffer.writeByte((byte) ('0' + value));
        } else if (value < 100) {
            writeBuffer.writeByte((byte) ('0' + value / 10));
            writeBuffer.writeByte((byte) ('0' + value % 10));
        } else {
            long tempValue = value;
            int numDigits = 0;
            while (tempValue > 0) {
                numDigits++;
                tempValue /= 10;
            }

            byte[] buffer = new byte[numDigits];
            for (int i = numDigits - 1; i >= 0; i--) {
                buffer[i] = (byte) ('0' + (value % 10));
                value /= 10;
            }
            writeBuffer.write(buffer);
        }
    }

    protected void writeString(String string) {
        int length = string.length();
        for (int charIndex = 0; charIndex < length; charIndex++) {
            char c = string.charAt(charIndex);
            byte b = (byte) c;
            writeBuffer.writeByte(b);
        }
    }

    protected enum HeaderWriteSource {
        WRITE, FLUSH, CLOSE
    }
}
