package labs;

import org.noear.solon.annotation.Component;
import org.noear.solon.data.sql.SqlCommand;
import org.noear.solon.data.sql.SqlExecutor;
import org.noear.solon.data.sql.impl.SimpleSqlExecutor;
import org.noear.solon.data.sql.intercept.SqlInterceptor;
import org.noear.solon.data.sql.intercept.SqlInvocation;

import javax.sql.DataSource;
import java.sql.SQLException;

@Component
public class SqlInterceptorImpl implements SqlInterceptor {

    @Override
    public Object doIntercept(SqlCommand cmd, SqlInvocation inv) throws SQLException {
        System.out.println(cmd);
        long start = System.currentTimeMillis();

        try {
            return inv.invoke(cmd);
        } finally {
            System.out.println(System.currentTimeMillis() - start);
        }
    }
}