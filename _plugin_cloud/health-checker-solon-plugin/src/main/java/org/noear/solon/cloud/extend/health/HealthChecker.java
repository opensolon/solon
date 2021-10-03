package org.noear.solon.cloud.extend.health;

import org.noear.solon.core.handle.Result;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * @author iYarnFog
 * @since 1.5
 * @date 2021/10/01 19:37
 */
public class HealthChecker {

    private static final HealthChecker healthChecker = new HealthChecker();
    private final Map<String, Supplier<Result<?>>> checkPoints = new ConcurrentHashMap<>();

    /**
     * 添加健康检查点
     * @param name 检查点名称
     * @param point 检查点处理器
     */
    public static void addPoint(String name, Supplier<Result<?>> point) {
        HealthChecker.getHealthChecker()
                .getCheckPoints()
                .put(name, point);
    }

    public HealthStatus checkAll() {
        HealthStatus healthStatus = new HealthStatus();

        this.checkPoints.forEach((name, point) -> {
            HealthStatus.CheckResult checkResult = new HealthStatus.CheckResult();
            checkResult.setName(name);
            try {
                Result<?> result = point.get();
                if (result.getCode() == Result.SUCCEED_CODE) {
                    checkResult.setStatus(HealthStatus.Code.UP);
                } else {
                    checkResult.setStatus(HealthStatus.Code.DOWN);

                    if (healthStatus.getOutcome() == null) {
                        healthStatus.setOutcome(HealthStatus.Code.DOWN);
                    } else {
                         if (healthStatus.getOutcome() != HealthStatus.Code.ERROR) {
                             healthStatus.setOutcome(HealthStatus.Code.DOWN);
                         }
                    }
                }
                checkResult.setData(result.getData());
            } catch (Exception exception) {
                checkResult.setStatus(HealthStatus.Code.ERROR);
                checkResult.setData(exception.getMessage());
                healthStatus.setOutcome(HealthStatus.Code.ERROR);
            }
            healthStatus.getChecks().add(checkResult);
        });

        if (healthStatus.getOutcome() == null) {
            healthStatus.setOutcome(HealthStatus.Code.UP);
        }
        return healthStatus;
    }

    public static HealthChecker getHealthChecker() {
        return healthChecker;
    }

    public Map<String, Supplier<Result<?>>> getCheckPoints() {
        return this.checkPoints;
    }
}
