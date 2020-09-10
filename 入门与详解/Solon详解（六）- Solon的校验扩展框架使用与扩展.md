在业务的实现过程中，尤其是对外接口开发，我们需要对请求进行大量的验证并返回错误状态码和描述。lombok 框架有很多很赞的注解，但是人家是throw一个异常，这与有些需求不一定能匹配。

该文将介绍Solon的扩展验证框架：`solon.extend.validation` 的使用和扩展（ `org.noear:solon-web` 已包含）。效果如下：

```java

@XValid
@XController
public class UserController {
    @NoRepeatSubmit  //重复提交验证
    @Whitelist     //白名单验证
    @NotNull({"name", "mobile", "icon", "code"})  //非NULL验证
    @Numeric({"code"})
    @XMapping("/user/add")
    public void addUser(String name, @Pattern("^http") String icon, int code, @Pattern("^13\\d{9}$") String mobile){
        //...
    }
}

```

相较于 Spring 的 Validator 是争对 Bean，Solon 则是争对 XContext（即http参数）。这点区别非常大，Solon 是在 XAction 执行之前对 http 参数进行校验。


| 注解  | 作用范围 |  说明 | 
| -------- | -------- | -------- | 
| Date    | 参数 | 校验注解的参数值为日期格式    | 
| DecimalMax(value)    | 参数 | 校验注解的参数值小于等于@ DecimalMax指定的value值     | 
| DecimalMin(value)     | 参数 | 校验注解的参数值大于等于@ DecimalMin指定的value值     | 
| Email    | 参数 | 校验注解的参数值为电子邮箱格式    | 
| Length(min, max)    | 参数 | 校验注解的参数值长度在min和max区间内     | 
| Max(value)    |  参数 | 校验注解的参数值小于等于@Max指定的value值     | 
| Min(value)     | 参数 | 校验注解的参数值大于等于@Min指定的value值     | 
| NoRepeatSubmit    | 控制器 或 动作  | 校验本次请求没有重复     | 
| NotBlank    | 动作 或 参数 | 校验注解的参数值不是空白     | 
| NotEmpty    | 动作 或 参数 | 校验注解的参数值不是空     | 
| NotNull   | 动作 或 参数 | 校验注解的参数值不是null     | 
| NotZero  | 动作 或 参数 | 校验注解的参数值不是0     | 
| Null    | 动作 或 参数 | 校验注解的参数值是null     | 
| Numeric    | 动作 或 参数 | 校验注解的参数值为数字格式    | 
| Pattern(value)    | 参数 | 校验注解的参数值与指定的正则表达式匹配    | 
| Whitelist    | 控制器 或 动作 | 校验本次请求在白名单范围内     | 
| XValid | 控制器 或 动作 | 为控制器 或 动作启用验证能力 |

可作用在 [动作 或 参数] 上的注解，加在动作上时可支持多个参数的校验。

### 一、定制使用

solon.extend.validation 通过 ValidatorManager，提供了一组定制和扩展接口。

#### 1、@NoRepeatSubmit 改为分布式锁

NoRepeatSubmit 默认使用了本地延时锁。如果是分布式环境，需要定制为分布式锁：

```java
public class NoRepeatLockNew implements NoRepeatLock {
    @Override
    public boolean tryLock(String key, int seconds) {
        //使用分布式锁
        //
        return LockUtils.tryLock(XWaterAdapter.global().service_name(), key, seconds);
    }
}

ValidatorManager.setNoRepeatLock(new NoRepeatLockNew());
```

#### 2、@Whitelist 实现验证

框架层面没办法为 Whitelist 提供一个名单库，所以需要通过一个接口实现完成对接。

```java
public class WhitelistCheckerNew implements WhitelistChecker {
    @Override
    public boolean check(Whitelist anno, XContext ctx) {
        String ip = IPUtils.getIP(ctx);

        return WaterClient.Whitelist.existsOfServerIp(ip);
    }
}

ValidatorManager.setWhitelistChecker(new WhitelistCheckerNew());
```

#### 3、改造校验输出

solon.extend.validation 默认输出 http 400 状态 + json；尝试改改去掉 http 400 状态。

```java
@XConfiguration
public class Config {
    @XBean  //Solon 的 @XBean 也支持空函数，为其它提运行申明
    public void adapter() {
        ValidatorManager.globalSet(new ValidatorManager((ctx, ano, rst, message) -> {
            ctx.setHandled(true);

            if (XUtil.isEmpty(message)) {
                message = new StringBuilder(100)
                        .append("@")
                        .append(ano.annotationType().getSimpleName())
                        .append(" verification failed")
                        .toString();
            }

            ctx.output(message);

            return true;
        }));
    }
}
```



### 二、添一个扩展注解

#### 1、先定义个校验注解 @Date 

偷懒一下，直接把自带的扔出来了。只要看着能自己搞就行了:-P

```java
@Target({ElementType.PARAMETER})   //只让它作用到参数，不管作用在哪，最终都是对XContext的校验
@Retention(RetentionPolicy.RUNTIME)
public @interface Date {
    @XNote("日期表达式, 默认为：ISO格式")  //用XNote注解，是为了用时还能看到这个注释
    String value() default  "";

    String message() default "";
}
```

#### 2、添加 @Date 的校验器实现类

```java
public class DateValidator implements Validator<Date> {
    public static final DateValidator instance = new DateValidator();


    @Override
    public String message(Date anno) {
        return anno.message();
    }

    @Override
    public XResult validate(XContext ctx, Date anno, String name, StringBuilder tmp) {
        String val = ctx.param(name);
        if (val == null || tryParse(anno, val) == false) {
            tmp.append(',').append(name);
        }

        if (tmp.length() > 1) {
            return XResult.failure(tmp.substring(1));
        } else {
            return XResult.succeed();
        }
    }

    private boolean tryParse(Date anno, String val) {
        try {
            if (XUtil.isEmpty(anno.value())) {
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(val);
            } else {
                DateTimeFormatter.ofPattern(anno.value()).parse(val);
            }

            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
```

#### 3、注册到校验管理器

```java
@XConfiguration
public class Config {
    @XBean
    public void adapter() {
        ValidatorManager.global().register(Date.class, DateValidator.instance);
    }
}
```

#### 4、使用一下

```java
@XValid
@XController
public class UserController extends VerifyController{
    @XMapping("/user/add")
    public void addUser(String name, @Date("yyyy-MM-dd") String birthday){
        //...
    }
}

```







