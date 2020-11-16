### 一、Web基础配置

```xml
//资源路径说明（不用配置；也不能配置）
resources/application.properties（或 application.yml） 为应用配置文件
resources/static/ 为静态文件根目标
resources/WEB-INF/view/ 为视图模板文件根目标（支持多视图共存）

//调试模式：
启动参数添加：-debug=1
```

#### 1、访问静态资源

Solon 的默认静态资源的路径为：（这个没得改，也不让改；为了简化套路）
```xml
resources/static/
```
在默放的处理规则下，所有请求，都会先执行静态文件代理。静态文件代理会检测是否存在静态文件，有则输出，没有则跳过处理。输出的静态文件会做304控制。

#### 2、自定义拦截器
Solon里所有的处理，都属于XHandler。可以用handler 的模式写，也可以用controller的模式写（XAction 也是 XHandler）
```java
// handler模式
//
Solon.global().before("/hello/", ctx->{
    if(ctx.param("name") == null){    
        ctx.setHandled(true);    //如果没有name, 则终止处理
    }
});

// controller模式（只是换了个注解）
//
@XInterceptor
public class HelloInterceptor  {
    @XMapping(value = "/hello/" , before = true)
    public void handle(XContext ctx, String name) {
        if(name == null){            
            ctx.setHandled(true);  //如果没有name, 则终止处理
        }
    }
}
```

#### 3、读取外部的配置文件
```java
@XConfiguration
public class Config{
    @XInject("${classpath:user.yml}")
    private UserModel user;
}
```

#### 4、HikariCP DataSource的配置

HiKariCP是数据库连接池的一个后起之秀，号称性能最好，可以完美地PK掉其他连接池。作者特别喜欢它。

##### a.引入依赖

```xml
<dependency>
    <groupId>com.zaxxer</groupId>
    <artifactId>HikariCP</artifactId>
    <version>3.3.1</version>
</dependency>

<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.18</version>
</dependency>
```

##### b.添加配置
```yml
test.db1:
    schema: "rock"
    jdbcUrl: "jdbc:mysql://localdb:3306/rock?useUnicode=true&characterEncoding=utf8&autoReconnect=true&rewriteBatchedStatements=true"
    driverClassName: "com.mysql.cj.jdbc.Driver"
    username: "demo"
    password: "UL0hHlg0Ybq60xyb"
    maxLifetime: 1000000
```

##### c.配置HikariCP数据源

建议这种操作，都安排在 @XConfiguration 配置类里执行。

```java
//注解模式
//
@XConfiguration
public class Config{
    // 同时支持 name 和 类型 两种方式注入（注入时没有name，即为按类型注入）
    //
    @XBean(value = "db1", typed = true)   
    pubblic DataSource dataSource(@XInject("${test.db1}") HikariDataSource ds){
        return ds;
    }
}

//静态类模式
//
//public class Config{
//    pubblic static HikariDataSource dataSource = Solon.cfg().getBean("test.db1", HikariDataSource.class);
//}
```

之后就可以通过@XInject注解得到这个数据源了。一般会改用加强注解对数据源进行自动转换；所有与solon对接的ORM框架皆采用这种方案。

#### 6、数据库操作框架集成

##### a.Weed3集成

Wee3是和Solon一样轻巧的一个框架，配置起来自然是简单的。

在pom.xml中引用weed3扩展组件
```xml
<dependency>
    <groupId>org.noear</groupId>
    <artifactId>weed3-solon-plugin</artifactId>
</dependency>
```

刚才的Config配置类即可复用。先以单数据源场景演示：
```java
//使用示例
@XController
public class DemoController{
    //@Db 按类型注入  //或 @Db("db1") 按名字注入  
    //@Db是weed3在Solon里的扩展注解 //可以注入 Mapper, BaseMapper, DbContext
    //
    @Db  
    BaseMapper<UserModel> userDao;
    
    @XMapping("/user/")
    pubblic UserModel geUser(long puid){
        return userDao.selectById(puid);
    }
}
```

##### b.Mybatis集成

在pom.xml中引用mybatis扩展组件

```xml
<dependency>
    <groupId>org.noear</groupId>
    <artifactId>mybatis-solon-plugin</artifactId>
</dependency>
```

添加mybatis mappers及相关的属性配置

```yml
mybatis.db1: #db1 要与数据源的bean name 对上
    typeAliases:    #支持包名 或 类名（.class 结尾）
        - "webapp.model"
    mappers:        #支持包名 或 类名（.class 结尾）或 xml（.xml结尾）；配置的mappers 会 mapperScan并交由Ioc容器托管
        - "webapp.dso.mapper.UserMapper.class"
```

刚才的Config配置类即也可复用
```java
//使用示例
@XController
public class DemoController{
    //@Db 是  mybatis-solon-plugin 里的扩展注解，可注入 SqlSessionFactory，SqlSession，Mapper
    //
    @Db    
    UserMapper userDao;  //UserMapper 已被 db1 自动 mapperScan 并已托管，也可用 @XInject 注入
    
    @XMapping("/user/")
    pubblic UserModel geUser(long puid){
        return userDao.geUser(puid);
    }
}
```

#### 7、使用事务

Solon中推荐使用@XTran注解来申明和管理事务。

> @XTran 支持多数据源事务，且使用方便

##### a.Weed3的事务
```java
//使用示例
@XController
public class DemoController{
    @Db  //@Db("db1") 为多数据源模式
    BaseMapper<UserModel> userDao;
    
    @XTran 
    @XMapping("/user/add")
    pubblic Long addUser(UserModel user){
        return userDao.insert(user, true); 
    }
}
```

##### b.Mybatis的事务
```java
@XController
public class DemoController{
    @Db  
    UserMapper userDao;  //UserMapper 已被 db1 mapperScan并已托管，也可用 @XInject 注入
    
    @XTran 
    @XMapping("/user/add")
    pubblic Long addUser(UserModel user){
        return userDao.addUser(user); 
    }
}
```

##### c.混合多源事务（这个时候，我们需要Service层参演了）
```java
@XService
public class UserService{
    @Db("db1")  //数据库1
    UserMapper userDao;  
    
    @XTran
    public void addUser(UserModel user){
        userDao.insert(user);
    }
}

@XService
public class AccountService{
    @Db("db2")  //数据库2
    AccountMapper accountDao;  
    
    @XTran
    public void addAccount(UserModel user){
        accountDao.insert(user);
    }
}

@XController
public class DemoController{
    @XInject
    AccountService accountService; 
    
    @XInject
    UserService userService; 
    
    @XTran
    @XMapping("/user/add")
    pubblic Long geUser(UserModel user){
        Long puid = userService.addUser(user);     //会执行db1事务
        
        accountService.addAccount(user);    //会执行db2事务
        
        return puid;
    }
}
```


#### 8、开始jsp支持（不建议用）

solon 的jsp支持，是基于视图模板的定位去处理的。根据启动器组件的不同，配置略有不同：
```xml
<!-- 添加 solon web 开发包 -->
<dependency>
    <groupId>org.noear</groupId>
    <artifactId>solon-web</artifactId>
    <type>pom</type>
    <exclusions>
        <!-- 排除默认的 jlhttp 启动器 -->
        <exclusion>
            <groupId>org.noear</groupId>
            <artifactId>solon.boot.jlhttp</artifactId>
        </exclusion>
    </exclusions>
</dependency>

<!-- 添加 jetty 或 undertow 启动器 -->
<dependency>
    <groupId>org.noear</groupId>
    <artifactId>solon.boot.jetty</artifactId>
</dependency>

<!-- 添加 jetty 或 undertow jsp 扩展支持包 -->
<dependency>
    <groupId>org.noear</groupId>
    <artifactId>solon.extend.jetty.jsp</artifactId>
</dependency>

<!-- 添加 jsp 视图引擎 -->
<dependency>
    <groupId>org.noear</groupId>
    <artifactId>solon.view.jsp</artifactId>
</dependency>
```

### 二、Web开发进阶

#### 1、Solon的MVC注解

##### a.@XController

控制器，只有一个注解。会自动通过不同的返回值做不同的处理
```java
@XController
public class DemoController{
    @XMapping("/test1/")
    public void test1(){
        //没返回
    }
    
    @XMapping("/test2/")
    public String test2(){
        return "返回字符串并输出";
    }
    
    @XMapping("/test3/")
    public UseModel test3(){
        return new UseModel(2, "noear"); //返回个模型，默认会渲染为json格式输出
    }
    
    @XMapping("/test4/")
    public ModelAndView test4(){
        return new ModelAndView("view path", map); //返回模型与视图，会被视图引擎渲染后再输出，默认是html格式
    }
}
```

##### b.@XMapping(value, method, produces)

默认只需要设定value值即可，method默认为XMethod.HTTP，即接收所有的http方法请求。

```
@XMapping("/user/")
```

#### 2、视图模板开发
freemaerker 视图

```html
<!doctype html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${title}</title>
</head>
<body>
<div>
     ${message}
</div>
</body>
</html>
```

控制器
```java
@XController
public class HelloworldController {
    @XMapping("/helloworld")
    public Object helloworld(){
        ModelAndView vm = new ModelAndView("helloworld.ftl");

        vm.put("title","demo");
        vm.put("message","hello world!");

        return vm;
    }
}
```

#### 3、模板调试模式（即：模板修改后，浏览器刷新即可）
```xml
//调试模式：
启动参数添加：-deubg=1 或 --deubg=1
```

#### 4、数据校验

Solon校验的是XContext上的参数（即http传入的参数），是在XAction参数注入之前的预处理。这与Spring验证框架区别是很大的。

```java
@XValid  //为控制器开启校验能力；也可以做用在一个基类上
@XController
public class ValidationController {

    @NoRepeatSubmit
    @NotNull({"name", "icon", "mobile"})
    @XMapping("/valid")
    public void test(String name, String icon, @Pattern("13\\d{9}") String mobile) {

    }
}
```

下面是更多的校验注解，可以研究一下：

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





#### 5、统一异常处理

```java
Solon.start(source, args)
    .onError(err->err.printStackTrace()); //或者记录到日志系统
```

### 三、打包与部署

#### 1、在pom.xml中配置打包的相关插件

Solon 的项目必须开启编译参数：-parameters

```xml
<build>
    <finalName>${project.name}</finalName>
    <plugins>

        <!-- 配置编译插件 -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.8.1</version>
            <configuration>
                <compilerArgument>-parameters</compilerArgument> 
                <source>1.8</source>
                <target>1.8</target>
                <encoding>UTF-8</encoding>
            </configuration>
        </plugin>

        <!-- 配置打包插件（设置主类，并打包成胖包） -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-assembly-plugin</artifactId>
            <version>3.3.0</version>
            <configuration>
                <finalName>${project.name}</finalName>
                <appendAssemblyId>false</appendAssemblyId>
                <descriptorRefs>
                    <descriptorRef>jar-with-dependencies</descriptorRef>
                </descriptorRefs>
                <archive>
                    <manifest>
                        <mainClass>webapp.DemoApp</mainClass>
                    </manifest>
                </archive>
            </configuration>
            <executions>
                <execution>
                    <id>make-assembly</id>
                    <phase>package</phase>
                    <goals>
                        <goal>single</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

#### 2、运行 maven 的 package 指令完成打包（IDEA的右侧边界面，就有这个菜单）

#### 3、终端运行：`java -jar DemoApp.jar` 即可启动
