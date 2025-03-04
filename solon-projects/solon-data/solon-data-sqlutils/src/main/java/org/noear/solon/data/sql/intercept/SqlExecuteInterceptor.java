package org.noear.solon.data.sql.intercept;

import org.noear.solon.data.sql.SqlCommand;
import org.noear.solon.lang.Preview;

import java.sql.SQLException;

/**
 * Sql 执行拦截器
 *
 * @author noear
 * @since 3.1
 */
@Preview("3.1")
public interface SqlExecuteInterceptor {
    /**
     * 拦截
     *
     * @param cmd      命令
     * @param executor 执行器
     */
    Object doIntercept(SqlCommand cmd, SqlExecutor executor) throws SQLException;
}