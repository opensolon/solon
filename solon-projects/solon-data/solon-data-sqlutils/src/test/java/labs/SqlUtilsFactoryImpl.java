package labs;

import org.noear.solon.annotation.Component;
import org.noear.solon.data.sql.SqlExecutor;
import org.noear.solon.data.sql.SqlUtilsFactory;
import org.noear.solon.data.sql.impl.SimpleSqlExecutor;

import javax.sql.DataSource;

@Component
public class SqlUtilsFactoryImpl implements SqlUtilsFactory {
    @Override
    public SqlExecutor create(DataSource ds, String sql) {
        //打印 sql
        return new SimpleSqlExecutor(ds, sql).intercept((cmd, inv) -> {
            System.out.println(cmd);
            long start = System.currentTimeMillis();

            try {
                return inv.invoke(cmd);
            } finally {
                System.out.println(System.currentTimeMillis() - start);
            }
        });
    }
}