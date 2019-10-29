package webapp.dso;

import com.zaxxer.hikari.HikariDataSource;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XMap;

public class DbUtil {

    //
    //使用连接池 配置 数据库上下文
    //
    private final static HikariDataSource dataSource(){
        XMap map = Aop.prop().getXmap("test.db");

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(map.get("url"));
        dataSource.setUsername(map.get("username"));
        dataSource.setPassword(map.get("password"));

        return dataSource;
    }

}
