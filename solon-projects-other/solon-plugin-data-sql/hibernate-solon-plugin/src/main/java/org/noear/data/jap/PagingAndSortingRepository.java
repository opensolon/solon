package org.noear.data.jap;


/**
 * 分页与排序仓库
 * */
public interface PagingAndSortingRepository<T,ID> extends CrudRepository<T,ID> {
    /**
     * 返回所有的实体，根据Sort参数提供的规则排序。
     */
    Iterable<T> findAll(Sort sort);

    /**
     * 返回一页实体，根据Pageable参数提供的规则进行过滤。
     */
    Page<T> findAll(Pageable pageable);
}
