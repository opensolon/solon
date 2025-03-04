package org.noear.solon.data.sql.intercept;

import org.noear.solon.data.sql.SqlCommand;
import org.noear.solon.lang.Preview;

import java.sql.SQLException;

/**
 * Sql 执行器
 *
 * @author noear
 * @since 3.1
 */
@Preview("3.1")
public interface SqlExecutor {
    /**
     * 执行
     *
     * @param cmd 命令
     */
    Object execute(SqlCommand cmd) throws SQLException;
}
