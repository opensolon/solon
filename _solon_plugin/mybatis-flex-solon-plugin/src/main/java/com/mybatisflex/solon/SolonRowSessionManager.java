package com.mybatisflex.solon;

import com.mybatisflex.core.row.RowSessionManager;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

/**
 * @author noear
 * @since 2.2
 */
public class SolonRowSessionManager implements RowSessionManager {
    @Override
    public SqlSession getSqlSession(SqlSessionFactory sqlSessionFactory, ExecutorType executorType) {
        return sqlSessionFactory.openSession(executorType);
    }

    @Override
    public void releaseSqlSession(SqlSession sqlSession, SqlSessionFactory sqlSessionFactory) {
        sqlSession.close();
    }
}
