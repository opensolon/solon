package com.swagger.demo.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "应用配置查询参数")
public class ApplicationQueryPo extends BaseQueryPo {
    @Schema(description = "应用名称")
    private String appName;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}