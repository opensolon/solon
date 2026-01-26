package demo.openapi3.base.test.domain;

import java.util.List;

public interface PageResult<T> {

    long getTotal();

    List<T> getData();
}
