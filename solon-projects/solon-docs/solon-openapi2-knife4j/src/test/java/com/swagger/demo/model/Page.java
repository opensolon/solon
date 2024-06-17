package com.swagger.demo.model;

import java.util.List;

/**
 * @author noear 2024/6/17 created
 */
public interface Page<T> {
    List<T> getData();
    int getPageNumber();
    int getPageSize();
}
