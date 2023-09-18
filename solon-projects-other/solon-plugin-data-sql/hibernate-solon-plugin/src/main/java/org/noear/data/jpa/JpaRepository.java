package org.noear.data.jpa;

/**
 * Jpa 仓库
 * */
public interface JpaRepository<T, ID> extends PagingAndSortingRepository<T, ID>, QueryByExampleExecutor<T> {
    /**
     * 将所有未决的更改刷新到数据库。
     */
    void flush();

    /**
     * 保存一个实体并立即将更改刷新到数据库。
     */
    <S extends T> S saveAndFlush(S entity);

    /**
     * 在一个批次中删除给定的实体集合，这意味着将产生一条单独的Query。
     */
    void deleteInBatch(Iterable<T> entities);

    /**
     * 在一个批次中删除所有的实体。
     */
    void deleteAllInBatch();

    /**
     * 根据给定的id标识符，返回对应实体的引用。
     */
    T getOne(ID id);
}
