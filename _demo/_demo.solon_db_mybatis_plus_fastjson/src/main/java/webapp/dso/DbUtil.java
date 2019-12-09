package webapp.dso;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.session.TransactionIsolationLevel;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.noear.solon.XUtil;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XMap;

import javax.sql.DataSource;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.sql.Connection;
import java.util.Properties;

public class DbUtil {

    //
    //使用连接池 配置 数据库上下文
    //
    public final static DataSource dataSource() {
        XMap map = Aop.prop().getXmap("test.db");

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(map.get("url"));
        dataSource.setUsername(map.get("username"));
        dataSource.setPassword(map.get("password"));

        return dataSource;
    }

    private static SqlSessionFactory _sqlSessionFactory;
    public final static SqlSessionFactory sqlSessionFactory() throws Exception {
        if (_sqlSessionFactory == null) {

            TransactionFactory transactionFactory = new JdbcTransactionFactory();
            Environment environment = new Environment("development", transactionFactory, dataSource());
            Configuration configuration = new Configuration(environment);

            configuration.addMappers("webapp.dso.db");

            _sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
        }

        return _sqlSessionFactory;
    }
}
