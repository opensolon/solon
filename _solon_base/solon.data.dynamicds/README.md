
DynamicDataSource 使用示例

配置示例（致少要有：type, strict, default）

* type: 设定数据源实现类型（一般用链接池 hikari,druid 之类）
* strict: 设定严格模式（默认：false）。启用后在未匹配到指定数据源时候会抛出异常, 不启用则使用默认数据源.
* default: 动态数据源的默认源

```yaml
demo.ds.db_user:
  type: "com.zaxxer.hikari.HikariDataSource" 
  strict: true
  default: 
    jdbcUrl: "xxx" #属性名要与 type 类的属性对上
    username: "xxx"
    paasword: "xxx"
    driverClassName: "xx"
  db_user_2:
    jdbcUrl: "xxx" #属性名要与 type 类的属性对上
    username: "xxx"
    paasword: "xxx"
    driverClassName: "xx"
```

代码示使你

```java
//配置数据源 bean
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
    
    @DynamicDs //使用 db_user 动态源内的 默认源
    public void addUser(){
        userMapper.inserUser();
    }
    
    @DynamicDs("db_user_1") //使用 db_user 动态源内的 db_user_1 源
    public void getUserList(){
        userMapper.selectUserList();
    }
    
    public void getUserList2(){
        DynamicDsHolder.set("db_user_2"); //使用 db_user 动态源内的 db_user_2 源
        userMapper.selectUserList();
    }
}
```