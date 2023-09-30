package org.noear.solon.cloud.metrics.export;

import org.noear.solon.core.AppContext;
import org.noear.solon.core.handle.Handler;

/**
 * 度量开放器（用于导出数据）
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
     * 是否支持输出
     * */
    boolean isSupported(AppContext appContext);
}
