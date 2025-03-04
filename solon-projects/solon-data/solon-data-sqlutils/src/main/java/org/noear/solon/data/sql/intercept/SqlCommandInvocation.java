package org.noear.solon.data.sql.intercept;

import org.noear.solon.core.util.RankEntity;
import org.noear.solon.data.sql.SqlCommand;

import java.sql.SQLException;
import java.util.List;

/**
 * Sql 命令调用者
 *
 * @author noear
 * @since 3.1
 */
public class SqlCommandInvocation {
    private final List<RankEntity<SqlCommandInterceptor>> executeInterceptors;
    private final SqlCallable callable;
    private final SqlCommand command;

    private int index;

    public SqlCommandInvocation(SqlCommand command, List<RankEntity<SqlCommandInterceptor>> executeInterceptors, SqlCallable callable) {
        this.executeInterceptors = executeInterceptors;
        this.callable = callable;
        this.command = command;
        this.index = 0;
    }

    /**
     * 命令
     */
    public SqlCommand getCommand() {
        return command;
    }

    /**
     * 调用
     */
    public Object invoke() throws SQLException {
        if (index < executeInterceptors.size()) {
            return executeInterceptors.get(index++).target.doIntercept(this);
        } else {
            return callable.call();
        }
    }
}