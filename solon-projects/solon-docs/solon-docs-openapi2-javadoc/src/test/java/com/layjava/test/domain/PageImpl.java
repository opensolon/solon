package com.layjava.test.domain;

/**
 * @author noear 2024/6/17 created
 */
public class PageImpl implements Page {
    private int pageNumber;
    private int pageSize;

    public PageImpl(int pageNumber, int pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    @Override
    public int getPageNumber() {
        return 0;
    }

    @Override
    public int getPageSize() {
        return 0;
    }
}
