package labs;

import org.noear.solon.Solon;
import org.noear.solon.data.sql.SqlConfiguration;
import org.noear.solon.data.sql.SqlExecutor;
import org.noear.solon.data.sql.SqlUtils;
import org.noear.solon.data.sql.SqlUtilsFactory;
import org.noear.solon.data.sql.impl.SimpleSqlUtils;

import javax.sql.DataSource;

/**
 * @author noear 2024/12/19 created
 */

public class SqlUtilsFactoryImpl implements SqlUtilsFactory {
    public static void main(String[] args){
        Solon.start(SqlUtilsFactoryImpl.class, args, app->{
            SqlConfiguration.setFactory(new SqlUtilsFactoryImpl());
        });
    }

    @Override
    public SqlUtils create(DataSource ds) {
        return new SqlUtilsImpl(ds);
    }

    public static class SqlUtilsImpl extends SimpleSqlUtils {
        public SqlUtilsImpl(DataSource ds) {
            super(ds);
        }

        @Override
        public SqlExecutor sql(String sql, Object... args) {
            //打印 sql
            System.out.println(sql);
            return super.sql(sql, args);
        }
    }
}
