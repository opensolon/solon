package com.swagger.demo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import org.noear.solon.validation.annotation.Min;
import org.noear.solon.validation.annotation.NotNull;

@Schema(name = "分页查询参数")
public class QueryParam {
    @Schema(description = "当前页号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "当前页号不能为空")
    @Min(value = 1, message = "当前页号不能小于1")
    private int currPage = 1;

    @Schema(description = "每页记录数", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "每页记录数不能为空")
    @Min(value = 1, message = "每页记录数不能小于1")
    private int pageSize = 10;

    @Schema(description = "排序列名，多列以“,”分隔")
    private String orderName;

    @Schema(description = "排序方式", allowableValues = {"asc", "desc"})
    private String orderCommand;

    public int getCurrPage() {
        return currPage;
    }

    public void setCurrPage(int currPage) {
        this.currPage = currPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public String getOrderCommand() {
        return orderCommand;
    }

    public void setOrderCommand(String orderCommand) {
        this.orderCommand = orderCommand;
    }
}