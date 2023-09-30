package org.noear.data.jpa;


public interface Pageable {
    int getPageNumber();

    int getPageSize();

    long getOffset();

    Sort getSort();
}
