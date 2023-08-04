package org.noear.solon.cloud.metrics.opener;

import org.noear.solon.core.AopContext;
import org.noear.solon.core.handle.Handler;

/**
 * 度量拉取器
 *
 * @author noear
 * @since 2.4
 */
public interface MeterOpener extends Handler {
    /**
     * 路径
     * */
    String path();

    /**
     * 是否已注册
     * */
    boolean isRegistered(AopContext aopContext);
}
