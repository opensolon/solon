package demo;

import demo.dso.UserMapper;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.aspect.annotation.Service;
import org.noear.solon.data.datasource.dynamic.DynamicDataSource;
import org.noear.solon.data.datasource.dynamic.DynamicDsUtils;
import org.noear.solon.data.datasource.dynamic.annotation.DynamicDs;

import javax.sql.DataSource;

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
}
@Service
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