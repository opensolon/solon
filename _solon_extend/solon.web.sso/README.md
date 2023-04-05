很多用户有 "简单的sso" 处理需求，特意写了个简单的

### 基本原理：

* 当前会话存一个 sso-key （标识）
* 执久层，存用户的 sso-key（标识）//一般，用 redis
* 每次登录后：当前会话记录 + 执久层记录 sso-key
* 检查时，用当前会话的 sso-key 与 执久层记录 sso-key 比较


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
    public SsoService ssoService(@Inject("${demo.redis}") RedisClient redisClient) {
        return new SsoServiceImpl(redisClient);
    }

    @Bean
    public LoginedChecker ssoLoginedChecker(@Inject SsoService ssoService) {
        return new SsoLoginedChecker(ssoService);
    }
}
```

* 应用 

```java
//登录示意代码
@Controller
public class LoginController {
    @Inject
    SsoService ssoService;

    @Mapping("/login")
    public void login(Context ctx){
        if (loginDo()) {
            //获取登录的用户id
            long userId = 0;

            //更新用户的单点登录标识
            ssoService.updateUserSsoKey(ctx, userId);
        }
    }
}

//使用示意代码
@Logined //可以使用验证注解了，并且是基于sso的
@Controller
public class AdminController extends BaseController{

}
```