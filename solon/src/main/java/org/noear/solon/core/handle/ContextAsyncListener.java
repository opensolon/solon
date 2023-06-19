package org.noear.solon.core.handle;

import java.io.IOException;

/**
 * 通用上下文接口 - 异步监听
 *
 * @author noear
 * @since 2.3
 */
public interface ContextAsyncListener {
    void onStart(Context ctx) throws IOException;

    void onComplete(Context ctx) throws IOException;

    void onTimeout(Context ctx) throws IOException;

    void onError(Context ctx, Throwable e) throws IOException;
}
