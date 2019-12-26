package webapp.dso;

import com.zaxxer.hikari.HikariDataSource;
import org.noear.solon.XApp;
import org.noear.solon.annotation.XBean;
import org.noear.solon.annotation.XConfiguration;
import org.noear.solon.core.XMap;
import org.noear.weed.DbContext;
import org.noear.weed.cache.ICacheServiceEx;
import org.noear.weed.cache.LocalCache;

@XConfiguration
public class DbConfig {

    //
    //缓存服务配置:: //新建个缓存服务，并通过nameSet 注册到 全局 libOfCache
    //
    public final static ICacheServiceEx cache = new LocalCache("test",60).nameSet("test");


    //
    //直接配置 数据库上下文
    //
    @XBean("db1")
    public DbContext db1(){
        return new DbContext(XApp.cfg().getProp("test.db1"));
    }


    //
    //使用连接池 配置 数据库上下文
    //
    private final static HikariDataSource dataSource(){
        XMap map = XApp.cfg().getXmap("test.db2");

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(map.get("url"));
        dataSource.setUsername(map.get("username"));
        dataSource.setPassword(map.get("password"));

        return dataSource;
    }
    public final static DbContext db2 = new DbContext().dataSourceSet(dataSource());
}
