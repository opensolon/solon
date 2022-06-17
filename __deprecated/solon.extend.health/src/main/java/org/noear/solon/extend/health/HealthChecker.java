package org.noear.solon.extend.health;

import org.noear.solon.core.handle.Result;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 健康检查器
 *
 * @author iYarnFog
 * @since 1.5
 * @date 2021/10/01 19:37
 */
public class HealthChecker {
    private static final Map<String, HealthIndicator> indicatorMap = new ConcurrentHashMap<>();


    /**
     * 添加指示器
     *
     * @param name  名称
     * @param indicator 指示器
     */
    public static void addIndicator(String name, HealthIndicator indicator) {
        indicatorMap.put(name, indicator);
    }

    /**
     * 检测
     */
    public static HealthCheckResult check() {
        Map<String, HealthCheckResult> details = new LinkedHashMap<>();

        HealthCheckResult healthResult = new HealthCheckResult();
        healthResult.setDetails(details);

        //todo:此处可能会异常？...by noear
        indicatorMap.forEach((name, indicator) -> {
            HealthCheckResult checkResult = checkItem(healthResult, indicator);
            details.put(name, checkResult);
        });

        return healthResult;
    }

    /**
     * 检测一个检测点
     */
    private static HealthCheckResult checkItem(HealthCheckResult healthResult, HealthIndicator indicator) {
        HealthCheckResult checkResult = new HealthCheckResult();

        try {
            Result<?> result = indicator.get();

            //设置当前检测数据
            checkResult.setDetails(result.getData());

            if (result.getCode() == Result.SUCCEED_CODE) {
                //如果当前检测成功
                checkResult.setStatus(HealthStatus.UP);
            } else {
                //如果当前检测失败，则设为下线
                checkResult.setStatus(HealthStatus.DOWN);

                //如果健康状态小于下线，则置为下线
                if (healthResult.getStatus().ordinal() < HealthStatus.DOWN.ordinal()) {
                    healthResult.setStatus(HealthStatus.DOWN);
                }
            }

        } catch (Throwable e) {
            checkResult.setStatus(HealthStatus.ERROR);
            checkResult.setDetails(e.getMessage());

            healthResult.setStatus(HealthStatus.ERROR);
        }

        //添加健康状态的详情记录
        return checkResult;
    }
}
