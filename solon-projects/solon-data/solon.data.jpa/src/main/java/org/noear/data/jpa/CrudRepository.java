package org.noear.data.jpa;

import java.util.Optional;

/**
 * 增查改删仓库
 * */
public interface CrudRepository<T,ID> {
    /**
     * 保存一个实体。
     */
    <S extends T> S save(S entity);

    /**
     * 保存提供的所有实体。
     */
    <S extends T> Iterable<S> saveAll(Iterable<S> entities);

    /**
     * 根据id查询对应的实体。
     */
    Optional<T> findById(ID id);

    /**
     * 根据id查询对应的实体是否存在。
     */
    boolean existsById(ID id);

    /**
     * 查询所有的实体。
     */
    Iterable<T> findAll();

    /**
     * 根据给定的id集合查询所有对应的实体，返回实体集合。
     */
    Iterable<T> findAllById(Iterable<ID> ids);

    /**
     * 统计现存实体的个数。
     */
    long count();

    /**
     * 根据id删除对应的实体。
     */
    void deleteById(ID id);

    /**
     * 删除给定的实体。
     */
    void delete(T entity);

    void deleteAllById(Iterable<? extends ID> ids);

    /**
     * 删除给定的实体集合。
     */
    void deleteAll(Iterable<? extends T> entities);

    /**
     * 删除所有的实体。
     */
    void deleteAll();
}
