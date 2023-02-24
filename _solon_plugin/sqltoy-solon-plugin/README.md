```xml
<dependency>
    <groupId>org.noear</groupId>
    <artifactId>sqltoy-solon-plugin</artifactId>
</dependency>
```

#### 1、描述

数据扩展插件，为 Solon Data 提供基于 [sqltoy](https://gitee.com/sagacity/sagacity-sqltoy) 的框架适配，以提供ORM支持。



#### 2、配置示例

```yml
demo.db1:
  schema: rock
  jdbcUrl: jdbc:mysql://localhost:3306/rock?useUnicode=true&characterEncoding=utf8&autoReconnect=true&rewriteBatchedStatements=true
  driverClassName: com.mysql.cj.jdbc.Driver
  username: root
  password: 123456
demo.db2:
  schema: rock
  jdbcUrl: jdbc:mysql://localhost:3306/rock?useUnicode=true&characterEncoding=utf8&autoReconnect=true&rewriteBatchedStatements=true
  driverClassName: com.mysql.cj.jdbc.Driver
  username: root
  password: 123456
```
#### 3、sqltoy配置说明：
sqltoy在配置项上基本一致，默认无需配置。sqltoy在spring boot中的配置有spring前缀,在solon中没有。
```properties
# sqltoy 在spring boot中的配置方式
spring.sqltoy.sqlResourcesDir=classpath:com/sqltoy/quickstart
spring.sqltoy.translateConfig=classpath:sqltoy-translate.xml


# sqltoy 在solon中的配置方式及默认值
sqltoy.sqlResourcesDir=classpath:sqltoy
sqltoy.translateConfig=classpath:sqltoy-translate.xml
# 当solon在debug模式下时，这个值会强制设置为true
sqltoy.debug=false
# 缓存类型,默认为Solon提供的缓存，即由CacheService提供
sqltoy.cacheType=solon
```



#### 4、应用示例

```java
//启动应用
public class DemoApp {
    public static void main(String[] args) {
        Solon.start(DemoApp.class, args);
    }
}

//配置数据源
@Configuration
public class Config {
    @Bean
    public DataSource db1(@Inject("${demo.db1}") HikariDataSource ds) {
        return ds;
    }
    //多数据源
    @Bean("tow")
    public DataSource db2(@Inject("${demo.db2}") HikariDataSource ds) {
        return ds;
    }

}


/**
 * import org.noear.solon.extend.sqltoy.annotation.Sql;
 * import org.noear.solon.extend.sqltoy.annotation.Param;
 * 
 * Mapper说明：
 * 
 * 一般情况下建议直接使用SqlToyLazyDao进行操作，里面封装了所有的CRUD操作。且在IDEA下有开发插件，可以直接跳转到xml中的sql定义。也不用写Mapper
 * 
 * 在@Sql中写 sqltoy Sql或者 sqlId
 * 没有@Sql注解时 通过方法名映射sqlId
 * 返回值分为Page,List,Entity和直接值(如：Integer等)，当其中Page,List的类型为直接值或返回值类型为直接值时,具体类型由sql中查询结果决定
 * 当为default方法时，直接调用default方法
 * Mapper中其他注解无效，如：@Tran,@Cache
 * 
 * Mapper参数规则：
 * 1、当参数列表中存在String,Date,Number,List,Array等类型参数或者以@Param标注的参数时，sql中参数为对应名字的参数
 * 2、切勿声明多个Page类型参数
 * 3、当不满足第一个条件时，以第一个非Page类型参数来作为Params传入sql进行查询
 */

public interface DemoMapper {
    // 查询单个User,getByUserName这个名字不重要，以@sql注解中内容为准
    @Sql("userList")
    UserVo getByUserName(String username);

    //不用@Sql注解的时候,就匹配方法名(userList)
    List<UserVo> userList(String username);

    // 也可直接写sql，由于参数列表中有String等类型,可在sql中直接使用参数名称
    @Sql("SELECT * FROM T_USER WHERE WHERE USER_NAME=:username #[and GENDER=:gender]")
    UserVo getByUserName2(String username,Integer gender);

    // 可查list,可以直接用Map或entity 做查询参数
    @Sql("SELECT * FROM T_USER WHERE WHERE USER_NAME=:username #[and GENDER=:gender]")
    List<UserVo> queryUser(UserVo query);
    
    //由于使用了Integer类型参数,user的值需要使用user.这样的前缀
    @Sql("SELECT * FROM T_USER WHERE WHERE USER_NAME=:user.username #[and GENDER=:gender]")
    List<UserVo> queryUser(UserVo user,Integer gender);
    // 也可分页
    @Sql("userList")
    Page<UserVo> pageUser(UserVo query,Page page);

    //可查询单值，这里要注意返回值，需要和数据库中值类型对应
    @Sql("select count(*) from t_user")
    Long count();
    //default 方法不处理，直接调用，可在default方法中调用本接口方法处理一些业务
    default long doSomeThingElse(){
        // Aop.get(XX.class) 获取其他bean来处理业务
        //dao().xxx  或通过SqlToyLazyDao来处理其他业务
        return count()+1;
    }

    //下面操作建议直接调使用dao接口调用，比如:insert操作，调用dao.save(user)还以返回user的主键(主键自增时比较有用)
    //但是使用mapper的语句时，返回的是影响数据的条数
    //更新返回影响数据条数
    @Sql("INSERT INTO T_USER(USER_NAME,GENDER) VALUES(:username,:gender)")
    long saveUser(User user);

    @Sql("UPDATE T_USER set gender=:gender where USER_NAME=:username")
    long updateUser(User user);

    @Sql("DELETE FROM T_USER WHERE USER_NAME=:username")
    long deleteUser(User user);

    @Sql("DELETE FROM T_USER WHERE USER_NAME=:username")
    long deleteUser1(String username);
    /**
     * 返回值为SqlToyLazyDao的方法可获取默认的dao
     * @return
     */
    SqlToyLazyDao dao();
}

```
```java
import org.noear.solon.extend.sqltoy.annotation.Db;
//应用
@ProxyComponent
public class AppService{
    @Db
    private SqlToyLazyDao sqlToyLazyDao;
    
    @Db("tow")//多数据源
    private SqlToyLazyDao sqlToyLazyDao2;
    
    @Db
    private DemoMapper demoMapper;
    @Db("tow")//Mapper 使用多数据
    private DemoMapper demoMapper2;
    
    //@Tran 使用Transaction
    public void test(){
        demoMapper.doSomeThingElse();
        sqlToyLazyDao.save(entity);
        //其他dao操作
    }
}
```

**具体可参考：**

- [https://gitee.com/noear/solon-examples/tree/main/4.Solon-Data/demo4061-sqltoy](https://gitee.com/noear/solon-examples/tree/main/4.Solon-Data/demo4061-sqltoy)
- [https://gitee.com/sagacity/sagacity-sqltoy](https://gitee.com/sagacity/sagacity-sqltoy)
- [sqltoy-online-doc](https://www.kancloud.cn/hugoxue/sql_toy/2390352) 
