package labs;

import org.noear.solon.annotation.Component;
import org.noear.solon.data.sql.SqlCommand;
import org.noear.solon.data.sql.intercept.SqlExecuteInterceptor;
import org.noear.solon.data.sql.intercept.SqlExecutor;

import java.sql.SQLException;

@Component
public class SqlExecuteInterceptorImpl implements SqlExecuteInterceptor {

    @Override
    public Object doIntercept(SqlCommand cmd, SqlExecutor executor) throws SQLException {
        System.out.println(cmd);
        long start = System.currentTimeMillis();

        try {
            return executor.execute(cmd);
        } finally {
            System.out.println(System.currentTimeMillis() - start);
        }
    }
}