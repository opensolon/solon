package demo.openapi3.base.test.domain;

import java.util.List;

public class PageResultImpl<T> implements PageResult<T>{
    private final long total;
    private final List<T> data;

    public PageResultImpl(long total, List<T> data) {
        this.total = total;

        this.data = data;
    }

    public long getTotal() {
        return total;
    }

    public List<T> getData() {
        return data;
    }
}
