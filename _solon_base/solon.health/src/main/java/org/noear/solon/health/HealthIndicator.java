package org.noear.solon.health;

import org.noear.solon.core.handle.Result;

/**
 * 健康指示器
 *
 * @author noear
 * @since 1.5
 */
@FunctionalInterface
public interface HealthIndicator {
    /**
     * 获取结果
     * */
    Result get();
}
