package com.swagger.demo.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "应用信息", description = "应用信息说明")
public class ApplicationVo {
    @Schema(description = "应用ID")
    private String id;
    @Schema(description = "应用名称")
    private String appName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

}