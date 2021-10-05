package org.noear.solon.extend.health;

import java.io.Serializable;

/**
 * 健康检查结果
 *
 * @author iYarnFog
 * @since 1.5
 */
public class HealthCheckResult implements Serializable {
    private String name;
    private HealthCode code;
    private Object data;

    /**
     * 设置检查点名称
     * */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 设置状态码
     * */
    public void setCode(HealthCode code) {
        this.code = code;
    }

    /**
     * 设置结果数据
     * */
    public void setData(Object data) {
        this.data = data;
    }
}
