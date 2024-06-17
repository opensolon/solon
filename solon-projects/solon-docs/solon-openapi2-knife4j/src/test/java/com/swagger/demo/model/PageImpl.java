package com.swagger.demo.model;

import java.util.List;

/**
 * @author noear 2024/6/17 created
 */
public class PageImpl<T> implements Page<T> {
    private int pageNumber;
    private int pageSize;
    private List<T> data;

    public PageImpl(int pageNumber, int pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    @Override
    public List getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    @Override
    public int getPageNumber() {
        return pageNumber;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }
}
