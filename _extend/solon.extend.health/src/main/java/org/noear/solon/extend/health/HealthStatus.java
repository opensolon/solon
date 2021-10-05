package org.noear.solon.extend.health;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 健康状态
 *
 * @author iYarnFog
 * @since 1.5
 * @date 2021/10/01 19:37
 */
public class HealthStatus implements Serializable {
    /**
     * 详情
     * */
    private List<HealthCheckResult> details = new ArrayList<>();
    /**
     * 状态码（默认为在线）
     * */
    private HealthCode code = HealthCode.UP;

    /**
     * 添加详情记录
     *
     * @param record 检测结果记录
     * */
    public void detailsAdd(HealthCheckResult record){
        details.add(record);
    }


    /**
     * 获取状态码
     * */
    public HealthCode getCode() {
        return code;
    }

    /**
     * 设置状态码
     * */
    public void setCode(HealthCode code) {
        this.code = code;
    }
}
