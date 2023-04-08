很多用户有 "简单的sso" 处理需求，特意写了个简单的

### 基本原理：

* 当前会话存一个 sdl-key （标识）
* 执久层，存用户的 sdl-key（标识）//一般，用 redis
* 每次登录后：当前会话记录 + 执久层记录 sdl-key
* 检查时，用当前会话的 sdl-key 与 执久层记录 sdl-key 比较


### 本案，通过适配 solon.validation 的 @Logined 检测能力实现 sso ：

* SsoService ：定义执久层接口
* SsoServiceImpl：做为 SsoService 默认实现
* SsoLoginedChecker ：与 solon.validation 对接

### 使用示例： (具体参考 test 下的代码)

* 配置

```yaml
demo.redis:
  server: "localhost:6379"
```

```java
@Configuration
public class Config {
    @Bean
    public SsoStorage ssoStorage(@Inject("${demo.redis}") RedisClient redisClient) {
        //或者使用 SsoStorageOfLocal 作临时测试
        return new SsoStorageOfRedis(redisClient);
    }

    @Bean
    public LoginedChecker ssoLoginedChecker() {
        return new SsoLoginedChecker();
    }
}
```

* 应用 

```java
//登录示意代码
@Controller
public class LoginController {
    @Mapping("/login")
    public void login(){
        if (loginDo()) {
            SsoUtil.login(1001);
        }
    }
}

//使用示意代码
@Logined //可以使用验证注解了，并且是基于sso的
@Controller
public class AdminController extends BaseController{
    @Mapping("test")
    public String test(){
        return "OK";
    }
}
```