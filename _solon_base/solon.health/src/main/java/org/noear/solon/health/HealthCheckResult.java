package org.noear.solon.health;

import java.io.Serializable;

/**
 * 健康检查结果
 *
 * @author iYarnFog
 * @since 1.5
 */
public class HealthCheckResult implements Serializable {
    private HealthStatus status = HealthStatus.UP;
    private Object details;

    /**
     * 获取状态
     * */
    public HealthStatus getStatus() {
        return status;
    }

    /**
     * 设置状态
     * */
    public void setStatus(HealthStatus status) {
        this.status = status;
    }

    /**
     * 设置详情
     * */
    public void setDetails(Object details) {
        this.details = details;
    }
}
