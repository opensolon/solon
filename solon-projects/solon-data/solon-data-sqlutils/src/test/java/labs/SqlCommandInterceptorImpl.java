package labs;

import org.noear.solon.annotation.Component;
import org.noear.solon.data.sql.intercept.SqlCommandInterceptor;
import org.noear.solon.data.sql.intercept.SqlCommandInvocation;

import java.sql.SQLException;

@Component
public class SqlCommandInterceptorImpl implements SqlCommandInterceptor {


    @Override
    public Object doIntercept(SqlCommandInvocation inv) throws SQLException {
        System.out.println(inv.getCommand());
        long start = System.currentTimeMillis();

        try {
            return inv.invoke();
        } finally {
            System.out.println(System.currentTimeMillis() - start);
        }
    }
}