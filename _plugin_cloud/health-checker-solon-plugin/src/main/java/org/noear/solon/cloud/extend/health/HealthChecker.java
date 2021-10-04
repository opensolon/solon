package org.noear.solon.cloud.extend.health;

import org.noear.solon.core.handle.Result;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * 健康检查器
 *
 * @author iYarnFog
 * @since 1.5
 * @date 2021/10/01 19:37
 */
public class HealthChecker {
    private static final Map<String, Supplier<Result>> checkPoints = new ConcurrentHashMap<>();


    /**
     * 添加健康检查点
     *
     * @param name  检查点名称
     * @param point 检查点处理器
     */
    public static void addPoint(String name, Supplier<Result> point) {
        checkPoints.put(name, point);
    }

    /**
     * 检测
     */
    public static HealthStatus check() {
        HealthStatus healthStatus = new HealthStatus();

        checkPoints.forEach((name, point) -> {
            checkOne(healthStatus, name, point);
        });

        return healthStatus;
    }

    /**
     * 检测一个检测点
     */
    private static void checkOne(HealthStatus healthStatus, String name, Supplier<Result> point) {
        HealthCheckResult checkResult = new HealthCheckResult();
        checkResult.setName(name);

        try {
            Result<?> result = point.get();

            //设置当前检测数据
            checkResult.setData(result.getData());

            if (result.getCode() == Result.SUCCEED_CODE) {
                //如果当前检测成功
                checkResult.setCode(HealthCode.UP);
            } else {
                //如果当前检测失败，则设为下线
                checkResult.setCode(HealthCode.DOWN);

                //如果健康状态小于下线，则置为下线
                if (healthStatus.getCode().ordinal() < HealthCode.DOWN.ordinal()) {
                    healthStatus.setCode(HealthCode.DOWN);
                }
            }

        } catch (Throwable e) {
            checkResult.setCode(HealthCode.ERROR);
            checkResult.setData(e.getMessage());

            healthStatus.setCode(HealthCode.ERROR);
        }

        //添加健康状态的详情记录
        healthStatus.detailsAdd(checkResult);
    }
}
