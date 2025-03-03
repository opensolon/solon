package org.noear.solon.data.sql.intercept;

import org.noear.solon.data.sql.SqlCommand;

import java.sql.SQLException;

/**
 * Sql 拦截器
 *
 * @author noear
 * @since 3.1
 */
public interface SqlInterceptor {
    /**
     * 拦截
     */
    Object doIntercept(SqlCommand cmd, SqlInvocation inv) throws SQLException;
}
