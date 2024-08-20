package org.noear.solon.rx;

/**
 * 可完成的发射器
 *
 * @author noear
 * @since 2.9
 */
public interface CompletableEmitter {
    void onError(Throwable cause);

    void onComplete();
}
