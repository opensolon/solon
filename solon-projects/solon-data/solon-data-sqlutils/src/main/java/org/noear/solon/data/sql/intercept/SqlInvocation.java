package org.noear.solon.data.sql.intercept;

import org.noear.solon.data.sql.SqlCommand;

import java.sql.SQLException;

/**
 * Sql 调用器
 *
 * @author noear
 * @since 3.1
 */
public interface SqlInvocation {
    Object invoke(SqlCommand cmd) throws SQLException;
}
