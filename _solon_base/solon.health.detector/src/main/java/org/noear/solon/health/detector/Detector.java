package org.noear.solon.health.detector;

import org.noear.solon.core.Lifecycle;
import org.noear.solon.core.handle.Result;
import org.noear.solon.health.HealthIndicator;

import java.util.Map;

/**
 * 检测器
 *
 * @author noear
 * @since 1.5
 * */
public interface Detector extends HealthIndicator, Lifecycle {
    String getName();

    Map<String, Object> getInfo();

    @Override
    default void start() throws Throwable {
        //空实现，别删
    }

    @Override
    default void stop() throws Throwable {
        //空实现，别删
    }

    @Override
    default Result get() {
        return Result.succeed(getInfo());
    }
}
