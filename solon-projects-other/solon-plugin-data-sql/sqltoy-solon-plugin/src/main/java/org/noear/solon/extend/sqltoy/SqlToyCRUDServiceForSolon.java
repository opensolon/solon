package org.noear.solon.extend.sqltoy;

import org.noear.solon.annotation.Singleton;
import org.noear.solon.data.annotation.Tran;
import org.noear.solon.annotation.Component;
import org.sagacity.sqltoy.dao.SqlToyLazyDao;
import org.sagacity.sqltoy.model.*;
import org.sagacity.sqltoy.service.SqlToyCRUDService;
import org.sagacity.sqltoy.service.impl.SqlToyCRUDServiceImpl;
import org.sagacity.sqltoy.translate.TranslateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;


/**
 *
 * @author 夜の孤城
 * @since 1.10
 * */
@Singleton(false) //因为会被多数据源使用，所以不能是单例
@Component
public class SqlToyCRUDServiceForSolon implements SqlToyCRUDService {
    protected final Logger logger = LoggerFactory.getLogger(SqlToyCRUDServiceImpl.class);
    protected SqlToyLazyDao sqlToyLazyDao;


    public void setSqlToyLazyDao(SqlToyLazyDao sqlToyLazyDao) {
        this.sqlToyLazyDao = sqlToyLazyDao;
    }

    @Tran
    public Object save(Serializable entity) {
        return this.sqlToyLazyDao.save(entity);
    }

    @Tran
    public <T extends Serializable> Long saveAll(List<T> entities) {
        return this.sqlToyLazyDao.saveAll(entities);
    }

    @Tran
    public <T extends Serializable> Long saveAllIgnoreExist(List<T> entities) {
        return this.sqlToyLazyDao.saveAllIgnoreExist(entities);
    }

    @Tran
    public Long update(Serializable entity, String... forceUpdateProps) {
        if (null == entity) {
            throw new IllegalArgumentException("update 数据对象为null!");
        } else {
            return this.sqlToyLazyDao.update(entity, forceUpdateProps);
        }
    }

    @Tran
    public Long updateCascade(Serializable entity, String... forceUpdateProps) {
        if (null == entity) {
            throw new IllegalArgumentException("update 数据对象为null!");
        } else {
            return this.sqlToyLazyDao.updateCascade(entity, forceUpdateProps, (Class[])null, (HashMap)null);
        }
    }

    @Tran
    public Long updateDeeply(Serializable entity) {
        if (null == entity) {
            throw new IllegalArgumentException("updateDeeply 数据对象为null!");
        } else {
            return this.sqlToyLazyDao.updateDeeply(entity);
        }
    }

    @Tran
    public <T extends Serializable> Long updateAll(List<T> entities, String... forceUpdateProps) {
        return this.sqlToyLazyDao.updateAll(entities, forceUpdateProps);
    }

    @Tran
    public <T extends Serializable> Long updateAllDeeply(List<T> entities) {
        return this.sqlToyLazyDao.updateAllDeeply(entities);
    }

    @Tran
    public Long saveOrUpdate(Serializable entity, String... forceUpdateProps) {
        if (null == entity) {
            throw new IllegalArgumentException("saveOrUpdate  数据对象为null!");
        } else {
            return this.sqlToyLazyDao.saveOrUpdate(entity, forceUpdateProps);
        }
    }

    @Tran
    public <T extends Serializable> Long saveOrUpdateAll(List<T> entities, String... forceUpdateProps) {
        return this.sqlToyLazyDao.saveOrUpdateAll(entities, forceUpdateProps);
    }

    @Tran
    public <T extends Serializable> T load(T entity) {
        return this.sqlToyLazyDao.load(entity);
    }

    @Tran
    public <T extends Serializable> T loadCascade(T entity) {
        return this.sqlToyLazyDao.loadCascade(entity, (LockMode)null, new Class[0]);
    }

    @Tran
    public <T extends Serializable> List<T> loadAll(List<T> entities) {
        return this.sqlToyLazyDao.loadAll(entities);
    }

    @Tran
    public <T extends Serializable> List<T> loadAllCascade(List<T> entities, Class... cascadeTypes) {
        return this.sqlToyLazyDao.loadAllCascade(entities, cascadeTypes);
    }

    @Tran
    public <T extends Serializable> List<T> loadByIds(Class<T> voClass, Object... ids) {
        return this.sqlToyLazyDao.loadByIds(voClass, ids);
    }

    @Tran
    public Long delete(Serializable entity) {
        return this.sqlToyLazyDao.delete(entity);
    }

    @Tran
    public <T extends Serializable> Long deleteAll(List<T> entities) {
        return this.sqlToyLazyDao.deleteAll(entities);
    }

    public Long deleteByIds(Class entityClass, Object... ids) {
        return this.sqlToyLazyDao.deleteByIds(entityClass, ids);
    }

    @Tran
    public void truncate(Class entityClass) {
        this.sqlToyLazyDao.truncate(entityClass);
    }

    @Tran
    public boolean isUnique(Serializable entity, String... paramsNamed) {
        return this.sqlToyLazyDao.isUnique(entity, paramsNamed);
    }

    @Tran
    public boolean wrapTreeTableRoute(Serializable entity, String pidField) {
        return this.sqlToyLazyDao.wrapTreeTableRoute((new TreeTableModel(entity)).pidField(pidField));
    }

    @Tran
    public boolean wrapTreeTableRoute(Serializable entity, String pidField, int appendIdSize) {
        return this.sqlToyLazyDao.wrapTreeTableRoute((new TreeTableModel(entity)).pidField(pidField).idLength(appendIdSize));
    }

    @Tran
    public <T> List<QueryResult<T>> parallQuery(List<ParallQuery> parallQueryList, String[] paramNames, Object[] paramValues) {
        return this.sqlToyLazyDao.parallQuery(parallQueryList, paramNames, paramValues, (ParallelConfig)null);
    }

    @Tran
    public <T> List<QueryResult<T>> parallQuery(List<ParallQuery> parallQueryList, String[] paramNames, Object[] paramValues, ParallelConfig parallelConfig) {
        return this.sqlToyLazyDao.parallQuery(parallQueryList, paramNames, paramValues, parallelConfig);
    }

    @Tran
    public <T> List<QueryResult<T>> parallQuery(List<ParallQuery> parallQueryList, Map<String, Object> paramsMap, ParallelConfig parallelConfig) {
        return this.sqlToyLazyDao.parallQuery(parallQueryList, paramsMap, parallelConfig);
    }

    public long generateBizId(String signature, int increment) {
        return this.sqlToyLazyDao.generateBizId(signature, increment);
    }

    public String generateBizId(Serializable entity) {
        return this.sqlToyLazyDao.generateBizId(entity);
    }

    public void translate(Collection dataSet, String cacheName, TranslateHandler handler) {
        this.sqlToyLazyDao.translate(dataSet, cacheName, (String)null, 1, handler);
    }

    public void translate(Collection dataSet, String cacheName, String dictType, Integer index, TranslateHandler handler) {
        this.sqlToyLazyDao.translate(dataSet, cacheName, dictType, index, handler);
    }

    public boolean existCache(String cacheName) {
        return this.sqlToyLazyDao.existCache(cacheName);
    }

    public Set<String> getCacheNames() {
        return this.sqlToyLazyDao.getCacheNames();
    }

    public String[] cacheMatchKeys(String matchRegex, CacheMatchFilter cacheMatchFilter) {
        return this.sqlToyLazyDao.cacheMatchKeys(matchRegex, cacheMatchFilter);
    }

    @Override
    public String[] cacheMatchKeys(CacheMatchFilter cacheMatchFilter, String... strings) {
        return this.sqlToyLazyDao.cacheMatchKeys(cacheMatchFilter, strings);
    }

    public <T extends Serializable> List<T> convertType(List sourceList, Class<T> resultType) {
        return this.sqlToyLazyDao.convertType(sourceList, resultType, new String[0]);
    }

    public <T extends Serializable> T convertType(Serializable source, Class<T> resultType) {
        return this.sqlToyLazyDao.convertType(source, resultType, new String[0]);
    }

    public <T extends Serializable> Page<T> convertType(Page sourcePage, Class<T> resultType) {
        return this.sqlToyLazyDao.convertType(sourcePage, resultType, new String[0]);
    }
}
