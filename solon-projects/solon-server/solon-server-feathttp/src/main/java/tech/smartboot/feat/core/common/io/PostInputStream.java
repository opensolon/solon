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

import tech.smartboot.feat.core.common.HttpStatus;
import tech.smartboot.feat.core.common.exception.FeatException;
import tech.smartboot.feat.core.common.exception.HttpException;
import tech.smartboot.feat.core.server.impl.HttpEndpoint;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
public class PostInputStream extends BodyInputStream {
    private final long maxPayload;
    private long remaining;

    public PostInputStream(HttpEndpoint session, long contentLength, long maxPayload) {
        super(session);
        this.remaining = contentLength;
        this.maxPayload = maxPayload;
    }

    @Override
    public int read(byte[] data, int off, int len) throws IOException {
        if (maxPayload > 0L && remaining > maxPayload) {
            throw new HttpException(HttpStatus.PAYLOAD_TOO_LARGE);
        }

        checkState();
        if (data == null) {
            throw new NullPointerException();
        }
        if (isFinished()) {
            return -1;
        }
        if (len == 0) {
            return 0;
        }

        boolean async = readListener != null;

        if (async && anyAreClear(state, FLAG_LISTENER_READY)) {
            throw new IllegalStateException();
        }

        int totalRead = 0;

        while (totalRead < len && remaining > 0) {

            ByteBuffer byteBuffer = session.readBuffer();

            if (!byteBuffer.hasRemaining()) {
                if (async) {
                    break;
                }

                int i = session.read();

                if (i == -1) {
                    return totalRead > 0 ? totalRead : -1;
                } else if (i <= 0) {
                    throw new FeatException("sync read returned unexpected result=" + i);
                }
            }

            int readLength = Math.min(len - totalRead, byteBuffer.remaining());

            if (remaining < readLength) {
                readLength = (int) remaining;
            }

            byteBuffer.get(data, off + totalRead, readLength);

            remaining -= readLength;
            totalRead += readLength;
        }

        if (remaining == 0) {
            setFlags(FLAG_FINISHED);
        }

        return totalRead;
    }
}
