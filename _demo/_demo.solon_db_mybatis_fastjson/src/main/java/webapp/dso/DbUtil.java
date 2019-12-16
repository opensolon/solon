package webapp.dso;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.noear.solon.XApp;
import org.noear.solon.core.XMap;

public class DbUtil {

    //
    //使用连接池 配置 数据库上下文
    //
    public final static HikariDataSource dataSource(){
        XMap map = XApp.cfg().getXmap("test.db");

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(map.get("url"));
        dataSource.setUsername(map.get("username"));
        dataSource.setPassword(map.get("password"));

        return dataSource;
    }

//    public SqlSessionFactory sqlSessionFactory() throws Exception {
//        Configuration cfg = new Configuration();
//        SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(cfg);
//        return sessionFactory;
//    }


    public final static SqlSessionFactory sqlSessionFactory() throws Exception{
        //InputStream is = XUtil.getResource("mybatis.xml").openStream();
        Configuration cfg = new Configuration();
        cfg.addLoadedResource("mybatis.xml");
        cfg.setEnvironment(new Environment("dev", null, dataSource()));

        // 构建sqlSession的工厂
        SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(cfg);
        return sessionFactory;
    }

//    public class SpringManagedTransactionFactory implements TransactionFactory {
//        public SpringManagedTransactionFactory() {
//        }
//
//        public Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit) {
//            return new SpringManagedTransaction(dataSource);
//        }
//
//        public Transaction newTransaction(Connection conn) {
//            throw new UnsupportedOperationException("New Spring transactions require a DataSource");
//        }
//
//        public void setProperties(Properties props) {
//        }
//    }
}
