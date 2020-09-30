### 注解对比

|  Solon 1.0.40 | Spring | JSR 330 | |
| -------- | -------- | -------- | -------- |
| @XInject *     | @Autowired     | @Inject     | 注入Bean（by type）    |
| @XInject("name")     | @Qualifier+@Autowired     | @Qualifier+@Inject     | 注入Bean（by name）    |
| @XInject("${name}")     | @Value("${name}")     | -     | 注入配置    |
| @XBean *     | @Component     | @Named     | 托管Bean     |
| @XSingleton     | @Scope(“singleton”)     | @Singleton     | 单例（Solon 默认是单例）     |
| @XSingleton(false)     | @Scope(“prototype”)     | -     | 非单例     |
| | | |
| @XInit *     | @PostConstruct     | -     | 构造完成并注入后的初始化     |
| @XConfiguration | @Configuration | - | 配置类 |
| @XController | @Controller,@RestController | - | 控制器类 |
| @XMapping | @RequestMapping,@GetMapping... | - | 映射 |


* Solon 的 @XInject 算是： Spring 的@Value、@Autowired、@Qualifier 三者的结合，但又不完全等价
* Solon 托管的 Bean 初始化顺序：new() - > @XInject - > @XInit -> Method@XBean
* 注1：Method@XBean，只执行一次（只在 @XConfiguration 里有效）
* 注2：@XInject 的参数注入，只在Method@XBean上有效
* 注3：@XInject 的类型注入，只在@XConfiguration类上有效

### 部分用例说明

> Solon 强调有节制的注解使用，尤其对于增加处理链路的操会比较节制。

* @XBean（Bean的托管：基于 name 或者 类型；且只记录第一次的注册）

```java
@XBean
public class UserService{
    @Db("db1")    //@Db为第三方扩展的注入注解
    BaseMapper<User> mapper;
    
    UserModel getUser(long puid){
        return db1.selectById(puid);
    }
}

/* @XBean("userService")
public class UserService{
    @Db("db1") 
    BaseMapper<User> mapper;
    
    UserModel getUser(long puid){
        return db1.selectById(puid);
    }
} */
```

* @XController

```
@XSingleton(false)    //非单例注解
@XController
public class UserController{
    @XInject("${message.notnull}")
    String message;
    
    @XInject
    UserService userService
    
    @XMapping("/user/{puid}")
    public Object user(Long puid){
        if(puid == null){
            return message;
        }
        return userService.getUser(puid);
    }
}
```

* @XConfiguration

```java
@XConfiguration
public class Config {
    @XBean("db1")
    public DataSource db1(@XInject("${test.db1}") HikariDataSource ds) {
        return ds;
    }
}

//系统异常监听（这个系统会发的，还可以监听不同的异常）
//
@XConfiguration
public class ThrowableListener implements XEventListener<Throwable> {
    WaterLogger log = new WaterLogger("rock_log");

    @Override
    public void onEvent(Throwable err) {
        XContext ctx = XContext.current();

        if (ctx != null) {
            String _in = ONode.stringify(ctx.paramMap());

            log.error(ctx.path(), _in, err);
        }
    }
}

//Bean扩展监听（为Mybatis配置类，添加插件）
//
@XConfiguration
public class  SqlHelperMybatisAutoConfiguration implements XEventListener<Configuration> {

    //...
    
    @Override
    public void onEvent(Configuration configuration) {
       SqlHelperMybatisPlugin plugin = new SqlHelperMybatisPlugin();
       //...
       configuration.addInterceptor(plugin);
    }    
}
```
