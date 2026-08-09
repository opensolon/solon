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

import io.github.smartboot.socket.transport.WriteBuffer;
import tech.smartboot.feat.core.common.codec.h2.codec.Http2Frame;
import tech.smartboot.feat.core.common.codec.h2.codec.SettingsFrame;
import tech.smartboot.feat.core.common.codec.h2.hpack.Decoder;
import tech.smartboot.feat.core.common.codec.h2.hpack.Encoder;
import tech.smartboot.feat.core.common.logging.Logger;
import tech.smartboot.feat.core.common.logging.LoggerFactory;
import tech.smartboot.feat.core.server.impl.HttpEndpoint;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
public class Http2Session {
    private static final Logger LOGGER = LoggerFactory.getLogger(Http2Session.class);
    public static final int STATE_FIRST_REQUEST = 0;
    public static final int STATE_PREFACE = 1;
    public static final int STATE_PREFACE_SM = 1 << 1;
    public static final int STATE_FRAME_HEAD = 1 << 2;
    public static final int STATE_FRAME_PAYLOAD = 1 << 3;
    private final ConcurrentHashMap<Integer, Http2Endpoint> streams = new ConcurrentHashMap<>();
    private final Decoder hpackDecoder = new Decoder(65536);
    private final Encoder hpackEncoder = new Encoder(65536);
    private final AtomicInteger pushStreamId = new AtomicInteger(0);
    private boolean settingEnabled = true;

    private final SettingsFrame settings = new SettingsFrame(0, true) {
        @Override
        public boolean decode(ByteBuffer buffer) {
            throw new IllegalStateException();
        }

        @Override
        public void writeTo(WriteBuffer writeBuffer) throws IOException {
            throw new IllegalStateException();
        }
    };
    private int streamId;
    private boolean prefaced;
    //    private final Http2ResponseImpl response;
    private Http2Frame currentFrame;
    private int state;
    private final HttpEndpoint request;

    public Http2Session(HttpEndpoint request) {
        this.request = request;
//        this.response = new Http2ResponseImpl(this);
    }

    public Http2Endpoint getStream(int streamId) {
        return streams.computeIfAbsent(streamId, k -> new Http2Endpoint(streamId, this, false));
    }


    public boolean isPrefaced() {
        return prefaced;
    }

    public void setPrefaced(boolean prefaced) {
        this.prefaced = prefaced;
    }


    public Http2Frame getCurrentFrame() {
        return currentFrame;
    }

    public void setCurrentFrame(Http2Frame currentFrame) {
        this.currentFrame = currentFrame;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Decoder getHpackDecoder() {
        return hpackDecoder;
    }

    public Encoder getHpackEncoder() {
        return hpackEncoder;
    }

    public HttpEndpoint getRequest() {
        return request;
    }

    /**
     * 更新服务端Settings配置
     */
    public void updateSettings(SettingsFrame settingsFrame) {
        settings.setEnablePush(settingsFrame.getEnablePush());
        settings.setHeaderTableSize(settingsFrame.getHeaderTableSize());
        settings.setInitialWindowSize(settingsFrame.getInitialWindowSize());
        settings.setMaxConcurrentStreams(settingsFrame.getMaxConcurrentStreams());
        settings.setMaxFrameSize(settingsFrame.getMaxFrameSize());
        settings.setMaxHeaderListSize(settingsFrame.getMaxHeaderListSize());
        LOGGER.info("updateSettings:" + settings);
    }

    public SettingsFrame getSettings() {
        return settings;
    }

    public AtomicInteger getPushStreamId() {
        return pushStreamId;
    }

    public boolean isSettingEnabled() {
        return settingEnabled;
    }

    public void settingDisable() {
        this.settingEnabled = false;
    }
}
