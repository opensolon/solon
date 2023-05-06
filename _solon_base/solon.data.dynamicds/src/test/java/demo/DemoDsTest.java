package demo;

import demo.dso.UserMapper;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.data.datasource.DsUtils;
import org.noear.solon.data.dynamicds.DynamicDataSource;
import org.noear.solon.data.dynamicds.DynamicDsUtils;
import org.noear.solon.data.dynamicds.DynamicDs;
import org.noear.solon.annotation.ProxyComponent;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;

/**
 * @author noear 2022/11/23 created
 */

public class DemoDsTest {
@Configuration
public class Config {
    @Bean("db_user")
    public DataSource dsUser(@Inject("$demo.ds.db_user}") DynamicDataSource dataSource) {
        return dataSource;
    }

    //@Bean("db_user")
    public DataSource dsUser2(@Inject("$demo.ds.db_user}") Properties props) {
        //手动构建，可以不用配置：type, strict
        Map<String, DataSource> dsMap = DsUtils.buildDsMap(props, DataSource.class);
        DataSource dsDef = dsMap.get("default");

        DynamicDataSource tmp = new DynamicDataSource();
        tmp.setStrict(true);
        tmp.setTargetDataSources(dsMap);
        tmp.setDefaultTargetDataSource(dsDef);

        return tmp;
    }
}
@ProxyComponent
public class UserService{
    @Db("db_user")
    UserMapper userMapper;
    @DynamicDs //使用默认源
    public void addUser(){
        userMapper.inserUser();
    }
    @DynamicDs("db_user_1") //使用 db_user_1 源
    public void getUserList(){
        userMapper.selectUserList();
    }
    public void getUserList2(){
        DynamicDsUtils.setCurrent("db_user_2"); //使用 db_user_2 源
        userMapper.selectUserList();
    }
}
}