/*
 *  Copyright (C) [2024] smartboot [zhengjunweimail@163.com]
 *
 *  企业用户未经smartboot组织特别许可，需遵循Apache-2.0开源协议合理合法使用本项目。
 *
 *   Enterprise users are required to use this project reasonably
 *   and legally in accordance with the Apache-2.0 open source agreement
 *  without special permission from the smartboot organization.
 */

package tech.smartboot.feat.core.server.impl;

import io.github.smartboot.socket.timer.HashedWheelTimer;
import io.github.smartboot.socket.timer.TimerTask;
import io.github.smartboot.socket.transport.AioSession;
import tech.smartboot.feat.core.common.*;
import tech.smartboot.feat.core.common.exception.FeatException;
import tech.smartboot.feat.core.common.exception.HttpException;
import tech.smartboot.feat.core.common.io.BodyInputStream;
import tech.smartboot.feat.core.common.io.ChunkedInputStream;
import tech.smartboot.feat.core.common.io.PostInputStream;
import tech.smartboot.feat.core.common.multipart.MultipartConfig;
import tech.smartboot.feat.core.common.multipart.Part;
import tech.smartboot.feat.core.server.HttpHandler;
import tech.smartboot.feat.core.server.HttpRequest;
import tech.smartboot.feat.core.server.ServerOptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
public final class HttpEndpoint extends Endpoint implements HttpRequest, Reset {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpEndpoint.class);

    private final DecodeContext decodeContext = new DecodeContext(DecodeContext.STATE_METHOD);
    private HttpHandler serverHandler;

    private List<Part> parts;

    private final HttpResponseImpl response;
    /**
     * 剩余可读字节数
     */
    private long remainingThreshold;
    private Upgrade upgrade;

    private TimerTask httpIdleTask;

    private BodyInputStream inputStream;
    private Map<String, String> trailerFields;
    private boolean keepAlive;

    void cancelHttpIdleTask() {
        synchronized (this) {
            if (httpIdleTask != null) {
                LOGGER.debug("cancel http idle monitor, request:{}", this);
                httpIdleTask.cancel();
                httpIdleTask = null;
            }
        }
    }

    HttpEndpoint(ServerOptions options, AioSession aioSession) {
        super(aioSession, options);
        this.remainingThreshold = options.getMaxRequestSize();
        this.response = new HttpResponseImpl(this);
        if (options.getIdleTimeout() > 0) {
            httpIdleTask = HashedWheelTimer.DEFAULT_TIMER.scheduleWithFixedDelay(() -> {
                LOGGER.debug("check httpIdle monitor");
                if (System.currentTimeMillis() - latestIo > options.getIdleTimeout()) {
                    LOGGER.debug("close http connection by idle monitor");
                    try {
                        aioSession.close();
                    } finally {
                        cancelHttpIdleTask();
                    }
                }
            }, options.getIdleTimeout(), TimeUnit.MILLISECONDS);
        }
    }

    public void setInputStream(BodyInputStream inputStream) {
        this.inputStream = inputStream;
    }

    public AbstractResponse getResponse() {
        return response;
    }


    @Override
    public BodyInputStream getInputStream() {
        if (inputStream != null) {
            return inputStream;
        }

        //如果一个消息即存在传输译码（Transfer-Encoding）头域并且也 Content-Length 头域，后者会被忽略。
        if (HeaderValue.TransferEncoding.CHUNKED.equalsIgnoreCase(getHeader(HeaderName.TRANSFER_ENCODING))) {
            return inputStream = new ChunkedInputStream(this, remainingThreshold, stringStringMap -> this.trailerFields = stringStringMap);
        }

        long contentLength = getContentLength();
        if (contentLength > 0) {
            inputStream = new PostInputStream(this, contentLength, remainingThreshold);
        } else if (getHeader(HeaderName.UPGRADE) != null) {
            inputStream = new PostInputStream(this, Long.MAX_VALUE, Long.MAX_VALUE);
        } else {
            inputStream = BodyInputStream.EMPTY_INPUT_STREAM;
        }
        return inputStream;
    }


    void decodeSize(int size) {
        remainingThreshold -= size;
        if (remainingThreshold < 0) {
            throw new HttpException(HttpStatus.PAYLOAD_TOO_LARGE);
        }
    }

    public HttpHandler getServerHandler() {
        return serverHandler;
    }

    public void setServerHandler(HttpHandler serverHandler) {
        this.serverHandler = serverHandler;
    }


    /**
     * Returns the Internet Protocol (IP) address of the client
     * or last proxy that sent the request.
     * For HTTP servlets, same as the value of the
     * CGI variable <code>REMOTE_ADDR</code>.
     *
     * @return a <code>String</code> containing the
     * IP address of the client that sent the request
     */

    public String getRemoteAddr() {
        if (remoteAddr != null) {
            return remoteAddr;
        }
        try {
            InetSocketAddress remote = aioSession.getRemoteAddress();
            InetAddress address = remote.getAddress();
            if (address == null) {
                remoteAddr = remote.getHostString();
            } else {
                remoteAddr = address.getHostAddress();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return remoteAddr;
    }


    public InetSocketAddress getRemoteAddress() {
        try {
            return aioSession.getRemoteAddress();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public InetSocketAddress getLocalAddress() {
        try {
            return aioSession.getLocalAddress();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns the fully qualified name of the client
     * or the last proxy that sent the request.
     * If the engine cannot or chooses not to resolve the hostname
     * (to improve performance), this method returns the dotted-string form of
     * the IP address. For HTTP servlets, same as the value of the CGI variable
     * <code>REMOTE_HOST</code>.
     *
     * @return a <code>String</code> containing the fully
     * qualified name of the client
     */

    public String getRemoteHost() {
        if (remoteHost != null) {
            return remoteHost;
        }
        try {
            remoteHost = aioSession.getRemoteAddress().getHostString();
        } catch (IOException e) {
            LOGGER.error("getRemoteHost error", e);
        }
        return remoteHost;
    }

    @Override
    public Map<String, String> getTrailerFields() {
        return trailerFields == null ? Collections.emptyMap() : trailerFields;
    }

    @Override
    public boolean isTrailerFieldsReady() {
        return !HeaderValue.TransferEncoding.CHUNKED.equals(getHeader(HeaderName.TRANSFER_ENCODING)) || trailerFields != null;
    }


    public DecodeContext getDecodeContext() {
        return decodeContext;
    }

    public Upgrade getUpgrade() {
        return upgrade;
    }

    public void setUpgrade(Upgrade upgrade) {
        this.upgrade = upgrade;
    }

    @Override
    public void upgrade(Upgrade upgrade) throws IOException {
        setUpgrade(upgrade);
        response.getOutputStream().disableChunked();
        //升级后取消http空闲监听
        cancelHttpIdleTask();
        upgrade.setRequest(this);
        upgrade.init(this, response);
        upgrade.onBodyStream(this.getAioSession().readBuffer());

    }

    public Collection<Part> getParts(MultipartConfig configElement) throws IOException {
        if (parts == null) {
            if (FeatUtils.isBlank(getContentType()) || !getContentType().startsWith(HeaderValue.ContentType.MULTIPART_FORM_DATA)) {
                throw new FeatException("Multipart is not supported for content type " + getContentType());
            }
            MultipartFormDecoder multipartFormDecoder = new MultipartFormDecoder(this, configElement);
            long remaining = getContentLength();
            if (configElement.getMaxRequestSize() > 0 && configElement.getMaxRequestSize() < remaining) {
                throw new HttpException(HttpStatus.PAYLOAD_TOO_LARGE);
            }
            int p = aioSession.readBuffer().position();
            while (!multipartFormDecoder.decode(aioSession.readBuffer(), this)) {
                remaining -= aioSession.readBuffer().position() - p;
                int readSize = aioSession.read();
                p = aioSession.readBuffer().position();
                if (readSize == -1) {
                    break;
                }
            }
            setInputStream(BodyInputStream.EMPTY_INPUT_STREAM);
            remaining -= aioSession.readBuffer().position() - p;
            if (remaining != 0) {
                throw new HttpException(HttpStatus.BAD_REQUEST);
            }
        }
        if (parts == null) {
            parts = new ArrayList<>();
        }
        return parts;
    }

    public void setPart(Part part) {
        if (parts == null) {
            parts = new ArrayList<>();
        }
        this.parts.add(part);
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public void reset() {
        super.reset();
        upgrade = null;
        serverHandler = null;
        keepAlive = false;
        remainingThreshold = options.getMaxRequestSize();
        method = null;
        decodeContext.setState(DecodeContext.STATE_METHOD);
        trailerFields = null;
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            inputStream = null;
        }
        response.reset();

        if (parts != null) {
            for (Part part : parts) {
                try {
                    part.delete();
                } catch (IOException ignore) {
                }
            }
            parts = null;
        }
    }
}
