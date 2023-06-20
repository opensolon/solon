package org.noear.solon.core.handle;

/**
 * 通用上下文接口 - 异步监听
 *
 * @author noear
 * @since 2.3
 */
public interface ContextAsyncListener {
    void onStart(Context ctx);

    void onComplete(Context ctx);

    void onTimeout(Context ctx);

    void onError(Context ctx, Throwable e);
}
