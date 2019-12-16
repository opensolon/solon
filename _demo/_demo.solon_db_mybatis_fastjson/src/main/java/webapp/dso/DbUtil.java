package webapp.dso;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.noear.solon.XApp;
import org.noear.solon.core.XMap;

import java.io.IOException;
import java.io.Reader;

public class DbUtil {

    //
    //使用连接池 配置 数据库上下文
    //
    public final static HikariDataSource dataSource() {
        XMap map = XApp.cfg().getXmap("test.db");

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(map.get("url"));
        dataSource.setUsername(map.get("username"));
        dataSource.setPassword(map.get("password"));

        return dataSource;
    }

    public static SqlSession getSqlSession() throws IOException {
        //通过配置文件获取数据库连接信息
        Reader reader = Resources.getResourceAsReader("mybatis.xml");

        //通过配置信息构建一个SqlSessionFactory
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);

        //通过SqlSessionFactory打开一个数据库会话
        SqlSession sqlsession = sqlSessionFactory.openSession();
        return sqlsession;
    }
}
