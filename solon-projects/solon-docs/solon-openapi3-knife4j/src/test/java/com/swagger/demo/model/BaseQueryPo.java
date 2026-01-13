package com.swagger.demo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import org.noear.solon.validation.annotation.NotNull;
import org.noear.solon.validation.annotation.Validated;

public abstract class BaseQueryPo {
    @Schema(description = "分页查询参数", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "分页查询参数不能为空")
    @Validated
    private QueryParam queryParam;

    public QueryParam getQueryParam() {
        return queryParam;
    }

    public void setQueryParam(QueryParam queryParam) {
        this.queryParam = queryParam;
    }
}