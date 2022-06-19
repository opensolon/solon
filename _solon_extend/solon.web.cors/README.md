 
## 使用简介

#### 方式一：加在控制器上，或方法上

```java
@CrossOrigin(origins = "*")
@Controller
public class Demo1Controller {
    @Mapping("/hello")
    public String hello() {
        return "hello";
    }
}

@Controller
public class Demo2Controller {
    @CrossOrigin(origins = "*")
    @Mapping("/hello")
    public String hello() {
        return "hello";
    }
}
```

#### 方式2：加在控制器基类

```java
@CrossOrigin(origins = "*")
public class BaseController {
    
}

@Controller
public class Demo3Controller extends BaseController{
    @Mapping("/hello")
    public String hello() {
        return "hello";
    }
}

```

#### 方式3：全局加在应用上

```java
public class App {
    public static void main(String[] args) {
        SolonApp app = Solon.start(App.class, args);

        //增加全局处理
        app.before(new CrossHandler().allowOrigin("*"));
        
        //或者增某段路径的处理
        app.before("/user/**", new CrossHandler().allowOrigin("*"));
    }
}
```