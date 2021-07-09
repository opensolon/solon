
## 国际化插件使用说明

### 一、引入插件包

```xml
<dependencies>
    <dependency>
        <groupId>org.noear</groupId>
        <artifactId>solon.i18n</artifactId>
    </dependency>
</dependencies>
```

### 二、配置国际化消息包

```
//默认国际化消息包配置（此为约定，不能改）
resources/i18n/messages.properties
resources/i18n/messages_en.properties
resources/i18n/messages_zh_CN.properties

//其它国际包内容包，例：
resources/i18n/login.properties
resources/i18n/login_en.properties
resources/i18n/login_zh_CN.properties
```

### 三、使用国际化配置

* 1.基于工具接口在代码里使用

```java
@Controller
public class DemoController {
    @Mapping("/demo/")
    public String demo(Context ctx) {
        return I18nUtil.getMessage(ctx, "login.title");
    }
}
```


* 2.基于@I18n注解在模板里使用，每个控制器独用一个国际化内容包（分开比较清爽）

添加国际化配置

```
resources/i18n/login.properties
resources/i18n/login_en.properties
resources/i18n/login_zh_CN.properties
```

```java
@I18n("i18n.login")
@Controller
public class LoginController {
    @Mapping("/login/")
    public ModelAndView login() {
        return new ModelAndView("login.ftl");
    }
}

```


* 3.基于@I18n注解在模板里使用，所有控制器使用统一的国际化内容包

```java
//此处使用默认的国际化内容包（i18n.messages）。也可换成 @I18n("i18n.test")
@I18n
public class ControllerBase{
    
}

@Controller
public class LoginController extends ControllerBase{
    @Mapping("/login/")
    public ModelAndView login() {
        return new ModelAndView("login.ftl");
    }
}

@Controller
public class UserController extends ControllerBase{
    @Mapping("/user/")
    public ModelAndView user() {
        return new ModelAndView("user.ftl");
    }
}
```

* 4.在各模板里的使用代码

beetl::
```html
i18n::${i18n["login.title"]}
i18n::${@i18n.get("login.title")}
i18n::${@i18n.getAndFormat("login.title",12,"a")}
```

enjoy::
```html
i18n::#(i18n.get("login.title"))
i18n::#(i18n.getAndFormat("login.title",12,"a"))
```

freemarker::
```html
i18n::${i18n["login.title"]}
i18n::${i18n.get("login.title")}
i18n::${i18n.getAndFormat("login.title",12,"a")}
```

thymeleaf::
```html
i18n::<span th:text='${i18n.get("login.title")}'></span>
i18n::<span th:text='${i18n.getAndFormat("login.title",12,"a")}'></span>
```

velocity::
```html
i18n::${i18n["login.title"]}
i18n::${i18n.get("login.title")}
i18n::${i18n.getAndFormat("login.title",12,"a")}
```