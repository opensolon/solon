package org.noear.solon.data.sql.intercept;

import org.noear.solon.core.util.RankEntity;
import org.noear.solon.data.sql.SqlCommand;

import java.sql.SQLException;
import java.util.List;

/**
 * Sql 执行包装器
 *
 * @author noear
 * @since 3.1
 */
public class SqlExecutorWrapper implements SqlExecutor{
    private final List<RankEntity<SqlExecuteInterceptor>> executeInterceptors;
    private final SqlExecutor executor;
    private int index;

    public SqlExecutorWrapper(List<RankEntity<SqlExecuteInterceptor>> executeInterceptors, SqlExecutor executor) {
        this.executeInterceptors = executeInterceptors;
        this.executor = executor;
        this.index = 0;
    }

    @Override
    public Object execute(SqlCommand cmd) throws SQLException {
        if (executor == null) {
            return executeInterceptors.get(index++).target.doIntercept(cmd, this);
        } else {
            if (index < executeInterceptors.size()) {
                return executeInterceptors.get(index++).target.doIntercept(cmd, this);
            } else {
                return executor.execute(cmd);
            }
        }
    }
}