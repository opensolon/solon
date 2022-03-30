package com.baomidou.mybatisplus.solon.utils;

import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.toolkit.*;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.reflection.ExceptionUtil;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.MyBatisExceptionTranslator;
import org.mybatis.spring.SqlSessionHolder;
import org.noear.solon.data.tran.TranUtils;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author noear
 * @since 1.6
 */
public class SqlHelper {
    /**
     * 批量操作 SqlSession
     *
     * @param clazz 实体类
     * @return SqlSession
     */
    public static SqlSession sqlSessionBatch(Class<?> clazz) {
        // TODO 暂时让能用先,但日志会显示Closing non transactional SqlSession,因为这个并没有绑定.
        return sqlSessionFactory(clazz).openSession(ExecutorType.BATCH);
    }

    /**
     * 获取SqlSessionFactory
     *
     * @param clazz 实体类
     * @return SqlSessionFactory
     * @since 3.3.0
     */
    public static SqlSessionFactory sqlSessionFactory(Class<?> clazz) {
        return GlobalConfigUtils.currentSessionFactory(clazz);
    }

    /**
     * 判断数据库操作是否成功
     *
     * @param result 数据库操作返回影响条数
     * @return boolean
     */
    public static boolean retBool(Integer result) {
        return null != result && result >= 1;
    }

    /**
     * 判断数据库操作是否成功
     *
     * @param result 数据库操作返回影响条数
     * @return boolean
     */
    public static boolean retBool(Long result) {
        return null != result && result >= 1;
    }

    /**
     * 返回SelectCount执行结果
     *
     * @param result ignore
     * @return int
     */
    public static long retCount(Long result) {
        return (null == result) ? 0 : result;
    }

    /**
     * 从list中取第一条数据返回对应List中泛型的单个结果
     *
     * @param list ignore
     * @param <E>  ignore
     * @return ignore
     */
    public static <E> E getObject(Log log, List<E> list) {
        return getObject(() -> log, list);
    }

    /**
     * @since 3.4.3
     */
    public static <E> E getObject(Supplier<Log> supplier, List<E> list) {
        if (CollectionUtils.isNotEmpty(list)) {
            int size = list.size();
            if (size > 1) {
                Log log = supplier.get();
                log.warn(String.format("Warn: execute Method There are  %s results.", size));
            }
            return list.get(0);
        }
        return null;
    }

    /**
     * 执行批量操作
     *
     * @param entityClass 实体
     * @param log         日志对象
     * @param consumer    consumer
     * @return 操作结果
     * @since 3.4.0
     */
    public static boolean executeBatch(Class<?> entityClass, Log log, Consumer<SqlSession> consumer) {
        SqlSessionFactory sqlSessionFactory = sqlSessionFactory(entityClass);
        boolean transaction = TranUtils.inTrans();

        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
        if (!transaction) {
            log.warn("SqlSession [" + sqlSession + "] Transaction not enabled");
        }
        try {
            consumer.accept(sqlSession);
            //非事务情况下，强制commit。
            sqlSession.commit(!transaction);
            return true;
        } catch (RuntimeException t) {
            throw t;
        } catch (Throwable t) {
            sqlSession.rollback();
            Throwable unwrapped = ExceptionUtil.unwrapThrowable(t);
            if (unwrapped instanceof RuntimeException) {
                throw (RuntimeException) unwrapped;
            } else {
                throw ExceptionUtils.mpe(unwrapped);
            }
        } finally {
            sqlSession.close();
        }
    }

    /**
     * 执行批量操作
     *
     * @param entityClass 实体类
     * @param log         日志对象
     * @param list        数据集合
     * @param batchSize   批次大小
     * @param consumer    consumer
     * @param <E>         T
     * @return 操作结果
     * @since 3.4.0
     */
    public static <E> boolean executeBatch(Class<?> entityClass, Log log, Collection<E> list, int batchSize, BiConsumer<SqlSession, E> consumer) {
        Assert.isFalse(batchSize < 1, "batchSize must not be less than one");
        return !CollectionUtils.isEmpty(list) && executeBatch(entityClass, log, sqlSession -> {
            int size = list.size();
            int idxLimit = Math.min(batchSize, size);
            int i = 1;
            for (E element : list) {
                consumer.accept(sqlSession, element);
                if (i == idxLimit) {
                    sqlSession.flushStatements();
                    idxLimit = Math.min(idxLimit + batchSize, size);
                }
                i++;
            }
        });
    }

    /**
     * 批量更新或保存
     *
     * @param entityClass 实体
     * @param log         日志对象
     * @param list        数据集合
     * @param batchSize   批次大小
     * @param predicate   predicate(新增条件) notNull
     * @param consumer    consumer（更新处理） notNull
     * @param <E>         E
     * @return 操作结果
     * @since 3.4.0
     */
    public static <E> boolean saveOrUpdateBatch(Class<?> entityClass, Class<?> mapper, Log log, Collection<E> list, int batchSize, BiPredicate<SqlSession, E> predicate, BiConsumer<SqlSession, E> consumer) {
        String sqlStatement = getSqlStatement(mapper, SqlMethod.INSERT_ONE);
        return executeBatch(entityClass, log, list, batchSize, (sqlSession, entity) -> {
            if (predicate.test(sqlSession, entity)) {
                sqlSession.insert(sqlStatement, entity);
            } else {
                consumer.accept(sqlSession, entity);
            }
        });
    }

    /**
     * 获取mapperStatementId
     *
     * @param sqlMethod 方法名
     * @return 命名id
     * @since 3.4.0
     */
    public static String getSqlStatement(Class<?> mapper, SqlMethod sqlMethod) {
        return mapper.getName() + StringPool.DOT + sqlMethod.getMethod();
    }
}
