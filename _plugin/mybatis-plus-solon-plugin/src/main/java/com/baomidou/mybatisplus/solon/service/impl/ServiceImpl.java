package com.baomidou.mybatisplus.solon.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.*;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.baomidou.mybatisplus.solon.service.IService;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;
import org.noear.solon.annotation.Inject;
import org.noear.solon.data.annotation.Tran;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author noear
 * @since 1.6
 */
public class ServiceImpl<M extends BaseMapper<T>, T> implements IService<T> {
    protected Log log = LogFactory.getLog(this.getClass());
    @Inject
    protected M baseMapper;
    protected Class<T> entityClass = this.currentModelClass();
    protected Class<M> mapperClass = this.currentMapperClass();

    public ServiceImpl() {
    }

    public M getBaseMapper() {
        return this.baseMapper;
    }

    public Class<T> getEntityClass() {
        return this.entityClass;
    }


    protected Class<M> currentMapperClass() {
        return (Class<M>)ReflectionKit.getSuperClassGenericType(this.getClass(), ServiceImpl.class, 0);
    }

    protected Class<T> currentModelClass() {
        return (Class<T>)ReflectionKit.getSuperClassGenericType(this.getClass(), ServiceImpl.class, 1);
    }


    @Tran
    public boolean saveBatch(Collection<T> entityList, int batchSize) {
        String sqlStatement = this.getSqlStatement(SqlMethod.INSERT_ONE);
        return this.executeBatch(entityList, batchSize, (sqlSession, entity) -> {
            sqlSession.insert(sqlStatement, entity);
        });
    }

    protected String getSqlStatement(SqlMethod sqlMethod) {
        return SqlHelper.getSqlStatement(this.mapperClass, sqlMethod);
    }

    @Tran
    public boolean saveOrUpdate(T entity) {
        if (null == entity) {
            return false;
        } else {
            TableInfo tableInfo = TableInfoHelper.getTableInfo(this.entityClass);
            Assert.notNull(tableInfo, "error: can not execute. because can not find cache of TableInfo for entity!", new Object[0]);
            String keyProperty = tableInfo.getKeyProperty();
            Assert.notEmpty(keyProperty, "error: can not execute. because can not find column for id from entity!", new Object[0]);
            Object idVal = tableInfo.getPropertyValue(entity, tableInfo.getKeyProperty());
            return !StringUtils.checkValNull(idVal) && !Objects.isNull(this.getById((Serializable)idVal)) ? this.updateById(entity) : this.save(entity);
        }
    }

    @Tran
    public boolean saveOrUpdateBatch(Collection<T> entityList, int batchSize) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(this.entityClass);
        Assert.notNull(tableInfo, "error: can not execute. because can not find cache of TableInfo for entity!", new Object[0]);
        String keyProperty = tableInfo.getKeyProperty();
        Assert.notEmpty(keyProperty, "error: can not execute. because can not find column for id from entity!", new Object[0]);
        return SqlHelper.saveOrUpdateBatch(this.entityClass, this.mapperClass, this.log, entityList, batchSize, (sqlSession, entity) -> {
            Object idVal = tableInfo.getPropertyValue(entity, keyProperty);
            return StringUtils.checkValNull(idVal) || CollectionUtils.isEmpty(sqlSession.selectList(this.getSqlStatement(SqlMethod.SELECT_BY_ID), entity));
        }, (sqlSession, entity) -> {
            MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap();
            param.put("et", entity);
            sqlSession.update(this.getSqlStatement(SqlMethod.UPDATE_BY_ID), param);
        });
    }

    @Tran
    public boolean updateBatchById(Collection<T> entityList, int batchSize) {
        String sqlStatement = this.getSqlStatement(SqlMethod.UPDATE_BY_ID);
        return this.executeBatch(entityList, batchSize, (sqlSession, entity) -> {
            MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap();
            param.put("et", entity);
            sqlSession.update(sqlStatement, param);
        });
    }

    public T getOne(Wrapper<T> queryWrapper, boolean throwEx) {
        return throwEx ? this.baseMapper.selectOne(queryWrapper) : SqlHelper.getObject(this.log, this.baseMapper.selectList(queryWrapper));
    }

    public Map<String, Object> getMap(Wrapper<T> queryWrapper) {
        return (Map)SqlHelper.getObject(this.log, this.baseMapper.selectMaps(queryWrapper));
    }

    public <V> V getObj(Wrapper<T> queryWrapper, Function<? super Object, V> mapper) {
        return SqlHelper.getObject(this.log, this.listObjs(queryWrapper, mapper));
    }


    protected <E> boolean executeBatch(Collection<E> list, int batchSize, BiConsumer<SqlSession, E> consumer) {
        return SqlHelper.executeBatch(this.entityClass, this.log, list, batchSize, consumer);
    }

    protected <E> boolean executeBatch(Collection<E> list, BiConsumer<SqlSession, E> consumer) {
        return this.executeBatch(list, 1000, consumer);
    }

    public boolean removeById(Serializable id) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(this.getEntityClass());
        return tableInfo.isWithLogicDelete() && tableInfo.isWithUpdateFill() ? this.removeById(id, true) : SqlHelper.retBool(this.getBaseMapper().deleteById(id));
    }

    @Tran
    public boolean removeByIds(Collection<?> list) {
        if (CollectionUtils.isEmpty(list)) {
            return false;
        } else {
            TableInfo tableInfo = TableInfoHelper.getTableInfo(this.getEntityClass());
            return tableInfo.isWithLogicDelete() && tableInfo.isWithUpdateFill() ? this.removeBatchByIds(list, true) : SqlHelper.retBool(this.getBaseMapper().deleteBatchIds(list));
        }
    }

    public boolean removeById(Serializable id, boolean useFill) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(this.entityClass);
        if (useFill && tableInfo.isWithLogicDelete() && !this.entityClass.isAssignableFrom(id.getClass())) {
            T instance = tableInfo.newInstance();
            tableInfo.setPropertyValue(instance, tableInfo.getKeyProperty(), new Object[]{id});
            return this.removeById(instance);
        } else {
            return SqlHelper.retBool(this.getBaseMapper().deleteById(id));
        }
    }

    @Tran
    public boolean removeBatchByIds(Collection<?> list, int batchSize) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(this.entityClass);
        return this.removeBatchByIds(list, batchSize, tableInfo.isWithLogicDelete() && tableInfo.isWithUpdateFill());
    }

    @Tran
    public boolean removeBatchByIds(Collection<?> list, int batchSize, boolean useFill) {
        String sqlStatement = this.getSqlStatement(SqlMethod.DELETE_BY_ID);
        TableInfo tableInfo = TableInfoHelper.getTableInfo(this.entityClass);
        return this.executeBatch(list, batchSize, (sqlSession, e) -> {
            if (useFill && tableInfo.isWithLogicDelete()) {
                if (this.entityClass.isAssignableFrom(e.getClass())) {
                    sqlSession.update(sqlStatement, e);
                } else {
                    T instance = tableInfo.newInstance();
                    tableInfo.setPropertyValue(instance, tableInfo.getKeyProperty(), new Object[]{e});
                    sqlSession.update(sqlStatement, instance);
                }
            } else {
                sqlSession.update(sqlStatement, e);
            }

        });
    }
}

