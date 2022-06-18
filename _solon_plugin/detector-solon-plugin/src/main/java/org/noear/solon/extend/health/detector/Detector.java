package org.noear.solon.extend.health.detector;

import org.noear.solon.core.Lifecycle;
import org.noear.solon.core.handle.Result;
import org.noear.solon.net.health.HealthIndicator;

import java.util.Map;

public interface Detector extends HealthIndicator, Lifecycle {
    String getName();

    Map<String, Object> getInfo();

    @Override
    default Result get() {
        return Result.succeed(getInfo());
    }
}
