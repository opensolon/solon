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

import tech.smartboot.feat.core.common.FeatUtils;
import tech.smartboot.feat.core.common.HttpStatus;
import tech.smartboot.feat.core.common.exception.HttpException;
import tech.smartboot.feat.core.server.impl.HttpEndpoint;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
public class ChunkedInputStream extends BodyInputStream {
    private static final int FLAG_CHUNKED_TRAILER = 1 << 4;
    private static final int FLAG_EXPECT_CR_LF = 1 << 5;
    //需要解析chunked长度
    protected static final int FLAG_READ_CHUNKED_LENGTH = 1 << 6;
    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream(8);

    private Map<String, String> trailerFields;
    /**
     * 剩余可读字节数
     */
    private long remainingThreshold;
    private final Consumer<Map<String, String>> consumer;
    private String trailerName;
    private int chunkedRemaining;

    public ChunkedInputStream(HttpEndpoint session, long maxPayload, Consumer<Map<String, String>> consumer) {
        super(session);
        this.remainingThreshold = maxPayload;
        this.consumer = consumer;
        setFlags(FLAG_READ_CHUNKED_LENGTH);
    }


    @Override
    public int read(byte[] data, int off, int len) throws IOException {
        checkState();
        if (data == null) {
            throw new NullPointerException();
        }
        if (len == 0) {
            return 0;
        }
        readChunkedLength();
        //仅在readListener情况下会存在true
        if (anyAreSet(state, FLAG_READ_CHUNKED_LENGTH)) {
            return 0;
        }
        if (isFinished()) {
            return -1;
        }


        ByteBuffer byteBuffer = session.readBuffer();
        if (chunkedRemaining > 0 && !byteBuffer.hasRemaining() && readListener == null) {
            session.read();
        }
        int readLength = Math.min(len, byteBuffer.remaining());
        readLength = Math.min(readLength, chunkedRemaining);
        byteBuffer.get(data, off, readLength);
        chunkedRemaining = chunkedRemaining - readLength;

        if (chunkedRemaining > 0) {
            return readLength + read(data, off + readLength, len - readLength);
        }
        setFlags(FLAG_EXPECT_CR_LF | FLAG_READ_CHUNKED_LENGTH);
        if (len == readLength) {
            return readLength;
        }
        int size = read(data, off + readLength, len - readLength);
        if (size <= 0) {
            return readLength;
        } else {
            return readLength + size;
        }
    }

    private void readChunkedLength() throws IOException {
        if (!anyAreSet(state, FLAG_READ_CHUNKED_LENGTH)) {
            return;
        }
        ByteBuffer byteBuffer = session.readBuffer();
        //前一个chunked解析完成，需要处理 CRLF
        if (anyAreSet(state, FLAG_EXPECT_CR_LF)) {
            if (byteBuffer.remaining() >= 2) {
                if (byteBuffer.get() == FeatUtils.CR && byteBuffer.get() == FeatUtils.LF) {
                    clearFlags(FLAG_EXPECT_CR_LF);
                } else {
                    throw new HttpException(HttpStatus.BAD_REQUEST);
                }
            } else {
                return;
            }
        }
        byteBuffer.mark();
        while (byteBuffer.hasRemaining()) {
            byte b = byteBuffer.get();
            if (b != FeatUtils.LF) {
                continue;
            }
            if (byteBuffer.get(byteBuffer.position() - 2) != FeatUtils.CR) {
                throw new HttpException(HttpStatus.BAD_REQUEST);
            }
            int p = byteBuffer.position() - 2;
            byteBuffer.reset();
            byte[] buffer = new byte[p - byteBuffer.position()];
            byteBuffer.get(buffer);
            chunkedRemaining = Integer.parseInt(new String(buffer), 16);
            remainingThreshold = remainingThreshold - 2 - buffer.length - chunkedRemaining;
            if (remainingThreshold < 0) {
                throw new HttpException(HttpStatus.PAYLOAD_TOO_LARGE);
            }
            if (byteBuffer.get() != FeatUtils.CR || byteBuffer.get() != FeatUtils.LF) {
                throw new HttpException(HttpStatus.BAD_REQUEST);
            }
            clearFlags(FLAG_READ_CHUNKED_LENGTH);
            if (chunkedRemaining > 0) {
                return;
            }
            setFlags(FLAG_CHUNKED_TRAILER);
            //trailerFields
            if (readListener == null) {
                while (true) {
                    parseTrailerFields();
                    if (anyAreSet(state, FLAG_CHUNKED_TRAILER)) {
                        session.read();
                    } else {
                        break;
                    }
                }
            } else {
                parseTrailerFields();
            }
            return;
        }

        byteBuffer.reset();
        //未注册readListener时，同步读取
        if (readListener == null) {
            int i = session.read();
            if (i == -1) {
                throw new IOException("inputStream is closed");
            } else {
                readChunkedLength();
            }
        }
    }

    private void parseTrailerFields() throws IOException {
        ByteBuffer byteBuffer = session.readBuffer();
        byteBuffer.mark();
        while (byteBuffer.hasRemaining()) {
            byte b = byteBuffer.get();
            if (b == FeatUtils.LF) {
                byteBuffer.mark();
                if (buffer.size() == 0) {
                    consumer.accept(trailerFields);
                    clearFlags(FLAG_CHUNKED_TRAILER);
                    setFlags(FLAG_FINISHED);
                    return;
                }
                trailerFields.put(trailerName, buffer.toString());
                buffer.reset();
            } else if (b == ':') {
                trailerName = buffer.toString();
                byteBuffer.mark();
                buffer.reset();
            } else if (b != FeatUtils.CR) {
                if (trailerFields == null) {
                    trailerFields = new HashMap<>();
                }
                buffer.write(b);
            }
        }
        byteBuffer.reset();
    }

}
