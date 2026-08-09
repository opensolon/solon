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

import io.github.smartboot.socket.transport.AioSession;
import tech.smartboot.feat.core.server.HttpRequest;
import tech.smartboot.feat.core.server.HttpResponse;
import tech.smartboot.feat.core.server.impl.HttpEndpoint;
import tech.smartboot.feat.core.server.impl.Upgrade;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
public abstract class BodyInputStream extends InputStream {
    public static final BodyInputStream EMPTY_INPUT_STREAM = new BodyInputStream(null) {


        @Override
        public int read(byte[] b, int off, int len) {
            return -1;
        }

        @Override
        public int available() {
            return 0;
        }

        @Override
        public boolean isFinished() {
            return true;
        }
    };
    protected final AioSession session;
    protected ReadListener readListener;
    protected volatile int state;
    protected static final int FLAG_LISTENER_READY = 1;
    protected static final int FLAG_FINISHED = 1 << 1;
    protected static final int FLAG_CLOSED = 1 << 2;
    private final HttpEndpoint request;


    protected static final AtomicIntegerFieldUpdater<BodyInputStream> stateUpdater = AtomicIntegerFieldUpdater.newUpdater(BodyInputStream.class, "state");

    public BodyInputStream(HttpEndpoint request) {
        if (request != null) {
            this.session = request.getAioSession();
            this.request = request;
        } else {
            this.session = null;
            this.request = null;
        }
    }


    @Override
    public final void close() throws IOException {
        setFlags(FLAG_CLOSED | FLAG_FINISHED);
    }

    @Override
    public final int read() throws IOException {
        byte[] b = new byte[1];
        int read = read(b);
        if (read == -1) {
            return -1;
        }
        return b[0] & 0xff;
    }

    /**
     * listener#onAllDataRead方法需要触发futuren.complete
     *
     * @param listener
     * @deprecated 适配 Jakarta servlet 规范，未来可能会被清除
     */
    public final void setReadListener(ReadListener listener) {
        if (listener == null) {
            throw new NullPointerException();
        }
        if (this.readListener != null) {
            throw new IllegalStateException();
        }
        readListener = new ReadListener() {
            @Override
            public void onDataAvailable() throws IOException {
                setFlags(FLAG_LISTENER_READY);
                listener.onDataAvailable();
                clearFlags(FLAG_LISTENER_READY);
                if (isFinished()) {
                    listener.onAllDataRead();
                }
            }

            @Override
            public void onError(Throwable t) {
                listener.onError(t);
            }
        };
        //复用Upgrade的能力实现ReadListener
        Upgrade upgrade = request.getUpgrade() == null ? new Upgrade() {
            @Override
            public void init(HttpRequest request, HttpResponse response) throws IOException {

            }

            @Override
            public void onBodyStream(ByteBuffer buffer) {

            }
        } : request.getUpgrade();
        request.setUpgrade(new Upgrade() {

            @Override
            public void init(HttpRequest request, HttpResponse response) throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public void onBodyStream(ByteBuffer buffer) {
                try {
                    readListener.onDataAvailable();
                } catch (Throwable throwable) {
                    readListener.onError(throwable);
                } finally {
                    upgrade.onBodyStream(buffer);
                }
            }
        });
    }

    public final ReadListener getReadListener() {
        return readListener;
    }

    public boolean isFinished() {
        return anyAreSet(state, FLAG_FINISHED);
    }

    protected final void checkState() throws IOException {
        if (anyAreSet(state, FLAG_CLOSED)) {
            throw new IOException("stream closed");
        }
    }

    public final boolean isReady() {
        //如果没有设置listener，将采用同步阻塞IO
        if (readListener == null) {
            return true;
        }
        return anyAreSet(state, FLAG_LISTENER_READY) && session.readBuffer().hasRemaining();
    }

    @Override
    public int available() throws IOException {
        checkState();
        return session.readBuffer().remaining();
    }

    protected static boolean anyAreClear(int var, int flags) {
        return (var & flags) != flags;
    }

    protected final void clearFlags(int flags) {
        int old;
        do {
            old = state;
        } while (!stateUpdater.compareAndSet(this, old, old & ~flags));
    }

    protected final void setFlags(int flags) {
        int old;
        do {
            old = state;
        } while (!stateUpdater.compareAndSet(this, old, old | flags));
    }

    protected final boolean anyAreSet(int var, int flags) {
        return (var & flags) != 0;
    }
}
