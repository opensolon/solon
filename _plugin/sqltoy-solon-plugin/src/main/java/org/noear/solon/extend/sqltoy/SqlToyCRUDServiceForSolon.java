package org.noear.solon.extend.sqltoy;

import org.noear.solon.core.Aop;
import org.noear.solon.data.annotation.Tran;
import org.noear.solon.extend.aspect.annotation.Service;
import org.sagacity.sqltoy.dao.SqlToyLazyDao;
import org.sagacity.sqltoy.model.*;
import org.sagacity.sqltoy.service.impl.SqlToyCRUDServiceImpl;
import org.sagacity.sqltoy.translate.TranslateHandler;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 *
 * @author 夜の孤城
 * @since 1.2
 * */
@Service
public class SqlToyCRUDServiceForSolon extends SqlToyCRUDServiceImpl {

    public SqlToyCRUDServiceForSolon() {
        Aop.getAsyn(SqlToyLazyDao.class, bw -> {
            sqlToyLazyDao = bw.raw();
        });
    }

    /*
     * (non-Javadoc)
     *
     * @see org.sagacity.sqltoy.service.SqlToyCRUDService#save(java.io.Serializable
     * )
     */
    @Override
    @Tran
    public Object save(Serializable entity) {
        return sqlToyLazyDao.save(entity);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.sagacity.sqltoy.service.SqlToyCRUDService#saveAll(java.util.List)
     */
    @Override
    @Tran
    public <T extends Serializable> Long saveAll(List<T> entities) {
        return sqlToyLazyDao.saveAll(entities);
    }

    @Override
    @Tran
    public <T extends Serializable> Long saveAllIgnoreExist(List<T> entities) {
        return sqlToyLazyDao.saveAllIgnoreExist(entities);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.sagacity.sqltoy.service.SqlToyCRUDService#update(java.io.Serializable ,
     * java.lang.String[])
     */
    @Override
    @Tran
    public Long update(Serializable entity, String... forceUpdateProps) {
        if (null == entity) {
            throw new IllegalArgumentException("update 数据对象为null!");
        }
        return sqlToyLazyDao.update(entity, forceUpdateProps);
    }

    @Override
    @Tran
    public Long updateCascade(Serializable entity, String... forceUpdateProps) {
        if (null == entity) {
            throw new IllegalArgumentException("update 数据对象为null!");
        }
        return sqlToyLazyDao.updateCascade(entity, forceUpdateProps, null, null);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.sagacity.sqltoy.service.SqlToyCRUDService#update(java.io.Serializable ,
     * boolean)
     */
    @Override
    @Tran
    public Long updateDeeply(Serializable entity) {
        if (null == entity) {
            throw new IllegalArgumentException("updateDeeply 数据对象为null!");
        }
        return sqlToyLazyDao.updateDeeply(entity);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.sagacity.sqltoy.service.SqlToyCRUDService#updateAll(java.util .List,
     * java.lang.String[])
     */
    @Override
    @Tran
    public <T extends Serializable> Long updateAll(List<T> entities, String... forceUpdateProps) {
        return sqlToyLazyDao.updateAll(entities, forceUpdateProps);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.sagacity.sqltoy.service.SqlToyCRUDService#updateAllDeeply(java
     * .util.List)
     */
    @Override
    @Tran
    public <T extends Serializable> Long updateAllDeeply(List<T> entities) {
        return sqlToyLazyDao.updateAllDeeply(entities);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.sagacity.sqltoy.service.SqlToyCRUDService#saveOrUpdate(java.io
     * .Serializable, java.lang.String[])
     */
    @Override
    @Tran
    public Long saveOrUpdate(Serializable entity, String... forceUpdateProps) {
        if (null == entity) {
            throw new IllegalArgumentException("saveOrUpdate  数据对象为null!");
        }
        return sqlToyLazyDao.saveOrUpdate(entity, forceUpdateProps);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.sagacity.sqltoy.service.SqlToyCRUDService#saveOrUpdateAll(java
     * .io.Serializable, java.lang.String[])
     */
    @Override
    @Tran
    public <T extends Serializable> Long saveOrUpdateAll(List<T> entities, String... forceUpdateProps) {
        return sqlToyLazyDao.saveOrUpdateAll(entities, forceUpdateProps);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.sagacity.sqltoy.service.SqlToyCRUDService#load(java.io.Serializable
     * )
     */
    @Override
    @Tran
    public <T extends Serializable> T load(T entity) {
        return sqlToyLazyDao.load(entity);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.sagacity.sqltoy.service.SqlToyCRUDService#loadCascade(java.io.
     * Serializable)
     */
    @Override
    @Tran
    public <T extends Serializable> T loadCascade(T entity) {
        return sqlToyLazyDao.loadCascade(entity, null);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.sagacity.sqltoy.service.SqlToyCRUDService#loadAll(java.util.List)
     */
    @Override
    @Tran
    public <T extends Serializable> List<T> loadAll(List<T> entities) {
        return sqlToyLazyDao.loadAll(entities);
    }

    @Override
    @Tran
    public <T extends Serializable> List<T> loadAllCascade(List<T> entities, final Class... cascadeTypes) {
        return sqlToyLazyDao.loadAllCascade(entities, cascadeTypes);
    }

    @Override
    @Tran
    public <T extends Serializable> List<T> loadByIds(Class<T> voClass, Object... ids) {
        return sqlToyLazyDao.loadByIds(voClass, ids);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.sagacity.sqltoy.service.SqlToyCRUDService#delete(java.io.Serializable )
     */
    @Override
    @Tran
    public Long delete(Serializable entity) {
        return sqlToyLazyDao.delete(entity);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.sagacity.sqltoy.service.SqlToyCRUDService#deleteAll(java.util .List)
     */
    @Override
    @Tran
    public <T extends Serializable> Long deleteAll(List<T> entities) {
        return sqlToyLazyDao.deleteAll(entities);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.sagacity.sqltoy.service.SqlToyCRUDService#truncate(java.io.
     * Serializable)
     */
    @Override
    @Tran
    public void truncate(final Class entityClass) {
        sqlToyLazyDao.truncate(entityClass);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.sagacity.sqltoy.service.SqlToyCRUDService#isUnique(java.io.
     * Serializable, java.lang.String[], java.lang.String)
     */
    @Override
    @Tran
    public boolean isUnique(Serializable entity, String... paramsNamed) {
        return sqlToyLazyDao.isUnique(entity, paramsNamed);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.sagacity.sqltoy.service.SqlToyCRUDService#wrapTreeTableRoute(
     * java.io.Serializable, java.lang.String)
     */
    @Override
    @Tran
    public boolean wrapTreeTableRoute(Serializable entity, String pidField) {
        return sqlToyLazyDao.wrapTreeTableRoute(new TreeTableModel(entity).pidField(pidField));
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.sagacity.sqltoy.service.SqlToyCRUDService#wrapTreeTableRoute(java.io.
     * Serializable, java.lang.String, int)
     */
    @Override
    @Tran
    public boolean wrapTreeTableRoute(Serializable entity, String pidField, int appendIdSize) {
        return sqlToyLazyDao.wrapTreeTableRoute(new TreeTableModel(entity).pidField(pidField).idLength(appendIdSize));
    }

    @Override
    @Tran
    public <T> List<QueryResult<T>> parallQuery(List<ParallQuery> parallQueryList, String[] paramNames,
                                                Object[] paramValues) {
        return sqlToyLazyDao.parallQuery(parallQueryList, paramNames, paramValues, null);
    }

    @Override
    @Tran
    public <T> List<QueryResult<T>> parallQuery(List<ParallQuery> parallQueryList, String[] paramNames,
                                                Object[] paramValues, ParallelConfig parallelConfig) {
        return sqlToyLazyDao.parallQuery(parallQueryList, paramNames, paramValues, parallelConfig);
    }

    @Override
    @Tran
    public <T> List<QueryResult<T>> parallQuery(List<ParallQuery> parallQueryList, Map<String, Object> paramsMap,
                                                ParallelConfig parallelConfig) {
        return sqlToyLazyDao.parallQuery(parallQueryList, paramsMap, parallelConfig);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.sagacity.sqltoy.service.SqlToyCRUDService#generateBizId(java.lang.String,
     * int)
     */
    @Override
    public long generateBizId(String signature, int increment) {
        return sqlToyLazyDao.generateBizId(signature, increment);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.sagacity.sqltoy.service.SqlToyCRUDService#generateBizId(java.io.
     * Serializable)
     */
    @Override
    public String generateBizId(Serializable entity) {
        return sqlToyLazyDao.generateBizId(entity);
    }

    @Override
    public void translate(Collection dataSet, String cacheName, TranslateHandler handler) {
        sqlToyLazyDao.translate(dataSet, cacheName, null, 1, handler);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.sagacity.sqltoy.service.SqlToyCRUDService#translate(java.util.Collection,
     * java.lang.String, java.lang.String, java.lang.Integer,
     * org.sagacity.sqltoy.plugin.TranslateHandler)
     */
    @Override
    public void translate(Collection dataSet, String cacheName, String dictType, Integer index,
                          TranslateHandler handler) {
        sqlToyLazyDao.translate(dataSet, cacheName, dictType, index, handler);
    }

    /**
     * @todo 判断缓存是否存在
     * @param cacheName
     * @return
     */
    @Override
    public boolean existCache(String cacheName) {
        return sqlToyLazyDao.existCache(cacheName);
    }

    @Override
    public Set<String> getCacheNames() {
        return sqlToyLazyDao.getCacheNames();
    }

    @Override
    public String[] cacheMatchKeys(String matchRegex, CacheMatchFilter cacheMatchFilter) {
        return sqlToyLazyDao.cacheMatchKeys(matchRegex, cacheMatchFilter);
    }

    @Override
    public <T extends Serializable> List<T> convertType(List sourceList, Class<T> resultType) {
        return sqlToyLazyDao.convertType(sourceList, resultType);
    }

    @Override
    public <T extends Serializable> T convertType(Serializable source, Class<T> resultType) {
        return sqlToyLazyDao.convertType(source, resultType);
    }

    @Override
    public <T extends Serializable> Page<T> convertType(Page sourcePage, Class<T> resultType) {
        return sqlToyLazyDao.convertType(sourcePage, resultType);
    }

}
