package com.swagger.demo.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

@Schema(name = "自定义分页查询结果")
public class CustomerQueryPageVo<T> {
    @Schema(description = "内容")
    private List<T> content = new ArrayList<>();
    @Schema(description = "总数")
    private long totalElements = 0;
    @Schema(description = "当前页号")
    private long currPage = 0;
    @Schema(description = "每页记录数")
    private long pageSize = 0;

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public long getCurrPage() {
        return currPage;
    }

    public void setCurrPage(long currPage) {
        this.currPage = currPage;
    }

    public long getPageSize() {
        return pageSize;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }
}