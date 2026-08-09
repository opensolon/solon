/*
 *  Copyright (C) [2024] smartboot [zhengjunweimail@163.com]
 *
 *  企业用户未经smartboot组织特别许可，需遵循Apache-2.0开源协议合理合法使用本项目。
 *
 *   Enterprise users are required to use this project reasonably
 *   and legally in accordance with the Apache-2.0 open source agreement
 *  without special permission from the smartboot organization.
 */

package tech.smartboot.feat.core.server.upgrade.http2;

import tech.smartboot.feat.core.common.FeatUtils;
import tech.smartboot.feat.core.common.codec.h2.codec.ContinuationFrame;
import tech.smartboot.feat.core.common.codec.h2.codec.DataFrame;
import tech.smartboot.feat.core.common.codec.h2.codec.HeadersFrame;
import tech.smartboot.feat.core.common.codec.h2.codec.Http2Frame;
import tech.smartboot.feat.core.common.io.FeatOutputStream;
import tech.smartboot.feat.core.server.impl.AbstractResponse;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
public final class Http2OutputStream extends FeatOutputStream {
    private final int streamId;
    private final Http2Session http2Session;
    private final AbstractResponse response;
    private final Http2Endpoint httpRequest;

    public Http2OutputStream(int streamId, Http2Endpoint httpRequest, AbstractResponse response, boolean push) {
        super(httpRequest.getAioSession().writeBuffer());
        this.httpRequest = httpRequest;
        this.response = response;
        disableChunked();
        this.http2Session = httpRequest.getSession();
        this.streamId = streamId;
    }

    protected void writeHeader(HeaderWriteSource source) throws IOException {
        if (committed) {
            if (source == HeaderWriteSource.CLOSE && !closed) {
//                writeBuffer.flush();
                DataFrame dataFrame1 = new DataFrame(streamId, DataFrame.FLAG_END_STREAM, 0);
                dataFrame1.writeTo(writeBuffer, FeatUtils.EMPTY_BYTE_ARRAY, 0, 0);

//                writeBuffer.flush();
            }
            return;
        }
        // Create HEADERS frame
        response.setHeader(":status", String.valueOf(response.getHttpStatus().value()));

        List<ByteBuffer> buffers = Util.HPackEncoder(http2Session.getHpackEncoder(), response.getHeaders());

        boolean multipleHeaders = buffers.size() > 1;

        HeadersFrame headersFrame = new HeadersFrame(streamId, multipleHeaders ? 0 : Http2Frame.FLAG_END_HEADERS, 0);
        headersFrame.setFragment(buffers.isEmpty() ? null : buffers.get(0));
        headersFrame.writeTo(writeBuffer);
        for (int i = 1; i < buffers.size() - 1; i++) {
            ContinuationFrame continuationFrame = new ContinuationFrame(streamId, 0, 0);
            continuationFrame.setFragment(buffers.get(i));
            continuationFrame.writeTo(writeBuffer);
        }
        if (multipleHeaders) {
            ContinuationFrame continuationFrame = new ContinuationFrame(streamId, Http2Frame.FLAG_END_HEADERS, 0);
            continuationFrame.setFragment(buffers.get(buffers.size() - 1));
            continuationFrame.writeTo(writeBuffer);
        }
//        writeBuffer.flush();
        System.err.println("StreamID: " + streamId + " Header已发送...");
        committed = true;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        writeHeader(HeaderWriteSource.WRITE);
        if (len == 0) {
            return;
        }
        DataFrame dataFrame = new DataFrame(streamId, 0, len);
        dataFrame.writeTo(writeBuffer, b, off, len);
        httpRequest.setLatestIo(FeatUtils.currentTime().getTime());
    }

}
