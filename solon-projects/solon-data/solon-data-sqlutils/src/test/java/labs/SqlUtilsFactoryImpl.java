package labs;

import org.noear.solon.annotation.Component;
import org.noear.solon.data.sql.SqlExecutor;
import org.noear.solon.data.sql.SqlUtilsFactory;

import javax.sql.DataSource;

/**
 * @author noear 2024/12/19 created
 */
@Component
public class SqlUtilsFactoryImpl implements SqlUtilsFactory {
    @Override
    public SqlExecutor create(DataSource ds, String sql, Object... args) {
        //打印 sql
        System.out.println(sql);
        return SqlUtilsFactory.super.create(ds, sql, args);
    }
}