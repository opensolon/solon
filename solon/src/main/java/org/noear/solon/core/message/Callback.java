package org.noear.solon.core.message;

/**
 * @author noear
 * @since 1.6
 */
public interface Callback {
    void onFailed(Throwable e);
    void onSuccess();
}
