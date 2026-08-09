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

import tech.smartboot.feat.core.common.*;
import tech.smartboot.feat.core.common.codec.h2.codec.*;
import tech.smartboot.feat.core.common.codec.h2.hpack.DecodingCallback;
import tech.smartboot.feat.core.server.HttpRequest;
import tech.smartboot.feat.core.server.HttpResponse;
import tech.smartboot.feat.core.server.impl.AbstractResponse;
import tech.smartboot.feat.core.server.impl.HttpEndpoint;
import tech.smartboot.feat.core.server.impl.HttpMessageProcessor;
import tech.smartboot.feat.core.server.impl.Upgrade;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
public class Http2Upgrade extends Upgrade {
    private static final byte[] H2C_PREFACE = "PRI * HTTP/2.0\r\n\r\nSM\r\n\r\n".getBytes();
    private static final int FRAME_HEADER_SIZE = 9;
    private Http2Session session;

    @Override
    public final void init(HttpRequest req, HttpResponse response) throws IOException {
        session = new Http2Session(request);
        if (HttpProtocol.HTTP_2 == request.getProtocol()) {
            if (!"PRI".equals(request.getMethod()) || !"*".equals(request.getUri()) || request.getHeaderSize() > 0) {
                throw new IllegalStateException();
            }
            session.setState(Http2Session.STATE_PREFACE_SM);
        } else {
            //解析 Header 中的 setting
            String http2Settings = request.getHeader(HeaderName.HTTP2_SETTINGS);
            byte[] bytes = Base64.getUrlDecoder().decode(http2Settings);
            ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
            SettingsFrame settingsFrame = new SettingsFrame(0, 0, bytes.length);
            settingsFrame.decode(byteBuffer);
            session.setState(Http2Session.STATE_PREFACE);
            //更新服务端的 setting
            session.updateSettings(settingsFrame);

            response.setHttpStatus(HttpStatus.SWITCHING_PROTOCOLS);
            response.setContentType(null);
            response.setHeader(HeaderName.UPGRADE, HeaderValue.Upgrade.H2C);
            response.setHeader(HeaderName.CONNECTION, HeaderValue.Connection.UPGRADE);
            OutputStream outputStream = response.getOutputStream();
            outputStream.flush();

            Http2Endpoint http2Request = session.getStream(1);
            http2Request.setRequestURI(request.getUri());
            http2Request.setMethod(request.getMethod());
            req.getHeaderNames().forEach(name -> http2Request.setHeader(name.toLowerCase(), request.getHeader(name)));
        }
    }

    @Override
    public final void onBodyStream(ByteBuffer buffer) {
        switch (session.getState()) {
            case Http2Session.STATE_FIRST_REQUEST: {
                return;
            }
            case Http2Session.STATE_PREFACE_SM: {
                if (buffer.remaining() < 6) {
                    return;
                }
                for (int i = H2C_PREFACE.length - 6; i < H2C_PREFACE.length; i++) {
                    if (H2C_PREFACE[i] != buffer.get()) {
                        throw new IllegalStateException();
                    }
                }
                session.setPrefaced(true);
                session.setState(Http2Session.STATE_FRAME_HEAD);
                onBodyStream(buffer);
                return;
            }
            case Http2Session.STATE_PREFACE: {
                if (buffer.remaining() < H2C_PREFACE.length) {
                    break;
                }
                for (byte b : H2C_PREFACE) {
                    if (b != buffer.get()) {
                        throw new IllegalStateException();
                    }
                }
                session.setPrefaced(true);
                session.setState(Http2Session.STATE_FRAME_HEAD);
                handleHttpRequest(session.getStream(1));
                break;
            }
            case Http2Session.STATE_FRAME_HEAD: {
                if (buffer.remaining() < FRAME_HEADER_SIZE) {
                    break;
                }
                Http2Frame frame = parseFrame(buffer);
                session.setCurrentFrame(frame);
                session.setState(Http2Session.STATE_FRAME_PAYLOAD);
            }
            case Http2Session.STATE_FRAME_PAYLOAD: {
                Http2Frame frame = session.getCurrentFrame();
                if (!frame.decode(buffer)) {
                    break;
                }
                session.setState(Http2Session.STATE_FRAME_HEAD);
                session.setCurrentFrame(null);
                try {
                    doHandler(frame, request);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                onBodyStream(buffer);
            }
        }
    }

    private void doHandler(Http2Frame frame, HttpEndpoint req) throws IOException {
        switch (frame.type()) {
            case Http2Frame.FRAME_TYPE_SETTINGS: {
                if (!session.isSettingEnabled()) {
                    throw new IOException();
                }
                SettingsFrame settingsFrame = (SettingsFrame) frame;
                if (settingsFrame.getFlag(SettingsFrame.ACK)) {
                    SettingsFrame settingAckFrame = new SettingsFrame(settingsFrame.streamId(), SettingsFrame.ACK, 0);
                    settingAckFrame.writeTo(req.getAioSession().writeBuffer());
                    req.getAioSession().writeBuffer().flush();
                    System.err.println("Setting ACK报文已发送");
                } else {
                    System.out.println("settingsFrame:" + settingsFrame);
                    session.updateSettings(settingsFrame);
                    settingsFrame.writeTo(req.getAioSession().writeBuffer());
                    req.getAioSession().writeBuffer().flush();
                    System.err.println("Setting报文已发送");
                }
            }
            break;
            case Http2Frame.FRAME_TYPE_WINDOW_UPDATE: {
                WindowUpdateFrame windowUpdateFrame = (WindowUpdateFrame) frame;
                System.out.println(windowUpdateFrame.getUpdate());
                SettingsFrame ackFrame = new SettingsFrame(windowUpdateFrame.streamId(), SettingsFrame.ACK, 0);
                ackFrame.writeTo(req.getAioSession().writeBuffer());
            }
            break;
            case Http2Frame.FRAME_TYPE_HEADERS: {
                session.settingDisable();
                HeadersFrame headersFrame = (HeadersFrame) frame;
                System.out.println("headerFrame Stream:" + headersFrame.streamId());
                Http2Endpoint request = session.getStream(headersFrame.streamId());
                request.checkState(Http2Endpoint.STATE_HEADER_FRAME);
                Map<String, HeaderValue> headers = request.getHeaders();
                session.getHpackDecoder().decode(headersFrame.getFragment(), headersFrame.getFlag(Http2Frame.FLAG_END_HEADERS), new DecodingCallback() {
                    @Override
                    public void onDecoded(CharSequence n, CharSequence v) {
                        System.out.println("name:" + n + " value:" + v);
                        String name = n.toString();
                        String value = v.toString();
                        if (name.charAt(0) == ':') {
                            switch (name) {
                                case ":method":
                                    request.setMethod(value);
                                    break;
                                case ":path":
                                    request.setRequestURI(value);
                                    break;
                                case ":scheme":
                                case ":authority":
                                    return;
                            }
                        } else {
                            headers.put(name, new HeaderValue(name, value));
                        }
                    }
                });
                if (headersFrame.getFragment().hasRemaining()) {
                    System.out.println("hasRemaining");
                }
                if (headersFrame.getFlag(Http2Frame.FLAG_END_HEADERS)) {
                    request.setState(Http2Endpoint.STATE_DATA_FRAME);
                    onHeaderComplete(request);
                    if (HttpMethod.GET.equals(request.getMethod())) {
                        handleHttpRequest(request);
                    } else if (request.getContentLength() > 0) {
                        request.setBody(new ByteArrayOutputStream((int) request.getContentLength()));
                    } else {
                        request.setBody(new ByteArrayOutputStream());
                    }
                }
                break;
            }
            case Http2Frame.FRAME_TYPE_DATA: {
                session.settingDisable();
                DataFrame dataFrame = (DataFrame) frame;
                Http2Endpoint request = session.getStream(dataFrame.streamId());
                request.checkState(Http2Endpoint.STATE_DATA_FRAME);
                request.getBody().write(dataFrame.getData());
                if (dataFrame.getFlag(DataFrame.FLAG_END_STREAM)) {
                    request.bodyDone();
                    handleHttpRequest(request);
                }
            }
            break;
            case Http2Frame.FRAME_TYPE_GOAWAY: {
                System.out.println("GoAwayFrame:" + ((GoAwayFrame) frame).getLastStream());
                break;
            }
            case Http2Frame.FRAME_TYPE_RST_STREAM: {
                ResetStreamFrame resetStreamFrame = (ResetStreamFrame) frame;
                System.out.println("RST_Stream, errorCode: " + resetStreamFrame.getErrorCode());
                break;
            }
            default:
                throw new IllegalStateException();
        }
    }


    private static Http2Frame parseFrame(ByteBuffer buffer) {
        int first = buffer.getInt();
        int length = first >> 8;
        int type = first & 0x0f;
        int flags = buffer.get();
        int streamId = buffer.getInt();
        if ((streamId & 0x80000000) != 0) {
            throw new IllegalStateException();
        }
        switch (type) {
            case Http2Frame.FRAME_TYPE_HEADERS:
                return new HeadersFrame(streamId, flags, length);
            case Http2Frame.FRAME_TYPE_SETTINGS:
                return new SettingsFrame(streamId, flags, length);
            case Http2Frame.FRAME_TYPE_WINDOW_UPDATE:
                return new WindowUpdateFrame(streamId, flags, length);
            case Http2Frame.FRAME_TYPE_DATA:
                return new DataFrame(streamId, flags, length);
            case Http2Frame.FRAME_TYPE_GOAWAY:
                return new GoAwayFrame(streamId, flags, length);
            case Http2Frame.FRAME_TYPE_RST_STREAM:
                return new ResetStreamFrame(streamId, flags, length);
        }
        throw new IllegalStateException("invalid type :" + type);
    }

    protected void onHeaderComplete(Http2Endpoint request) throws IOException {

    }

    public final void handleHttpRequest(Http2Endpoint abstractRequest) {
        AbstractResponse response = abstractRequest.getResponse();
        CompletableFuture<Void> future = new CompletableFuture<>();
        try {
            handle(abstractRequest, future);
            abstractRequest.getResponse().close();
        } catch (Throwable e) {
            HttpMessageProcessor.responseError(response, e);
        }
    }

    public void handle(HttpRequest request) throws Throwable {
    }

    public void handle(HttpRequest request, CompletableFuture<Void> completableFuture) throws Throwable {
        try {
            handle(request);
        } finally {
            completableFuture.complete(null);
        }
    }
}
