
[![Maven Central](https://img.shields.io/maven-central/v/org.noear/solon.svg)](https://mvnrepository.com/search?q=g:org.noear%20AND%20solon)

` QQ交流群：22200020 `

# Solon for java

一个插件式的 Java 轻量开发框架。**更快、更小、更自由！**

支持jdk8+；主框架0.1mb；组合不同的插件应对不同需求；方便定制；快速开发。

* 克制、简洁、开放
* Http、WebSocket、Socket 三种信号统一的开发体验（俗称：三源合一）
* 支持注解与手动两种模式，按需自由操控
* Not Servlet，可以适配任何基础通讯框架（所以：最小0.2m运行rpc架构）
* 自建 IOC & AOP容器，支持REST API、MVC、Job、Remoting、MicoService等开发
* 集合 Handler + Context 和 Listener + Message 两种架构模式；强调插件式扩展；适应不同的应用场景
* 插件可扩展可切换：启动插件，扩展插件，序列化插件，数据插件，会话状态插件，视图插件(可共存) 等...
* 使用感觉与 Spring Boot 近似，迁移成本低


### Hello world：

```java
//Handler 模式：
public class App{
    public static void main(String[] args){
        SolonApp app = Solon.start(App.class,args);
        
        app.get("/",(c)->c.output("Hello world!"));
    }
}

//Controller 模式：(mvc or rest-api)
@Controller
public class App{
    public static void main(String[] args){
        Solon.start(App.class,args);
    }
  
    //限定 put 方法类型
    @Put
    @Mapping("/")
    public String hello(String name){
        return "Hello " + name;
    }
}

//Remoting 模式：(rpc)
@Mapping("/")
@Remoting
public class App implements HelloService{
    public static void main(String[] args){
        Solon.start(App.class,args);
    }

    @Override
    public String hello(){
        return "Hello world!";
    }
}
```


### 主框架及快速集成开发包：

###### 主框架

| 组件 | 说明 |
| --- | --- |
| org.noear:solon-parent | 框架版本管理 |
| org.noear:solon | 主框架 |
| org.noear:nami | 伴生框架（做为solon remoting 的客户端）|

###### 快速集成开发包及相互关系

| 组件 | 说明 |
| --- | --- |
| org.noear:solon-lib | 快速开发基础集成包 |
| org.noear:solon-api | solon-lib + http boot；快速开发接口应用 |
| org.noear:solon-web | solon-api + freemarker + sessionstate；快速开发WEB应用 |
| org.noear:solon-beetl-web | solon-api + beetl + beetlsql + sessionstate；快速开发WEB应用 |
| org.noear:solon-enjoy-web | solon-api + enjoy + arp + sessionstate；快速开发WEB应用 |
| org.noear:solon-rpc | solon-api + nami；快速开发RPC应用 |
| org.noear:solon-cloud | solon-rpc + consul；快速开发微服务应用 |

### 快速了解Solon的材料：

##### [《Solon 特性简集，相较于 Springboot 有什么区别？》](https://my.oschina.net/noear/blog/4863844)
##### [《Solon Cloud 分布式服务开发套件清单，感觉受与 Spring Cloud 的不同》](https://my.oschina.net/noear/blog/5039169)
##### [《Solon 的想法与架构笔记》](https://my.oschina.net/noear/blog/4980834)
##### [《Solon 生态插件清单》](https://my.oschina.net/noear/blog/5053423)

### 丰富的示例与文章
* 项目内的：[_test](./_test/) 和 [_demo](./_demo/)
* 更多示例：[solon_demo](https://gitee.com/noear/solon_demo) 、 [solon_api_demo](https://gitee.com/noear/solon_api_demo)  、 [solon_rpc_demo](https://gitee.com/noear/solon_rpc_demo) 、 [solon_socketd_demo](https://gitee.com/noear/solon_socketd_demo) 、 [solon_cloud_demo](https://gitee.com/noear/solon_cloud_demo)
* 更多文章：[https://www.cnblogs.com/noear/](https://www.cnblogs.com/noear/)

### 快速入门示例
* Web 示例（mvc）
```xml
<parent>
    <groupId>org.noear</groupId>
    <artifactId>solon-parent</artifactId>
    <version>1.5.15</version>
</parent>

<dependencies>
    <dependency>
        <groupId>org.noear</groupId>
        <artifactId>solon-web</artifactId>
    </dependency>
</dependencies>

```
```
//资源路径说明（不用配置）
resources/app.properties（或 app.yml） 为应用配置文件
resources/static/ 为静态文件根目标
resources/WEB-INF/view/ 为视图文件根目标（支持多视图共存）

//调试模式：
启动参数添加：-debug=1
```
```java
public class App{
    public static void main(String[] args){
        Solon.start(App.class, args);
    }
}

/*
 * mvc控制器
 */
@Controller
public class DemoController{
    //for http
    @Mapping("/hallo/{u_u}")
    public ModelAndView hallo(String u_u){
        return new ModelAndView("hallo");
    }
    
    /*
    //for web socket （需添加：solon.boot.websocket 插件）
    @Mapping(value="/hallo/{u_u}", method = MethodType.WEBSOCKET)
    public ModelAndView hallo_ws(String u_u){
        return new ModelAndView("hallo");
    }
    */
}
```

* Remoting 示例（rpc）

```java
// - interface : 定义协议
public interface DemoService{
    void setName(Integer user_id, String name);
}

// - server : 实现协议
@Mapping("/demo/*")
@Remoting
public class DemoServiceImp implements DemoService{
    public void setName(int user_id, String name){
        
    }
}

// - client - 简单示例
//注入模式
//@NamiClient("http://127.0.0.1:8080/demo/") 
//DemoService client;

//手动模式
DemoService client = Nami.builder().url("http://127.0.0.1:8080/demo/").create(DemoService.class); 
client.setName(1,'');


```

* 获取应用配置
```java
//手动模式
Solon.cfg().get("app_key"); //=>String
Solon.cfg().getInt("app_id",0); //=>int
Solon.cfg().getProp("xxx.datasource"); //=>Properties

//注入模式
@Configuration //or @Controller, or @Component
public class Config{
    @Inject("${app_key}")
    String app_key;

    @Inject("${app_title:Solon}")
    String app_title;
}
```

* 事务与缓存控制（+验证）
```java
@Valid
@Controller
public class DemoController{
    @Db
    BaseMapper<UserModel> userService;
    
    @NotZero("user_id")
    @CacheRemove(tags = "user_${user_id}")
    @Tran
    @Mapping("/user/update")
    public void udpUser(int user_id, UserModel user){
        userService.updateById(user);
    }

    @NotZero("user_id")
    @Cache(tags = "user_${user_id}")
    public UserModel getUser(int user_id){
        return userService.selectById(user_id);
    }
}
```

* 文件上传与输出
```java
@Controller
public class DemoController{
    @Mapping("/file/upload")
    public void upload(UploadedFile file){
        IoUtil.save(file.content, "/data/file_" + file.name);
    }

    @Mapping("/file/down")
    public void down(Context ctx, String path){
        URL uri = Utils.getResource(path);

        ctx.contentType("json/text");
        ctx.output(uri.openStream());
    }
}
```

* Servlet 注解支持
```java
@WebFilter("/hello/*")
public class HelloFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        servletResponse.getWriter().write("Hello，我把你过滤了");
    }
}
```

* Quartz 定时任务
```java
@Quartz(cron7x = "0 0/1 * * * ? *")
public class HelloTask implements Runnable {
    public static void main(String[] args){
        Solon.start(QuartzRun2.class,args);
    }
    
    @Override
    public void run() {
        System.out.println("Hello world");
    }
}
```

* 体外扩展加载 jar
```
demoApp.jar             #主程序
ext/                    #扩展目录
ext/ext.markdown.jar    #MD格式支持扩展包
```

* 单链接双向RPC（客户端链上服务端之后，形成双向RPC）
```java 
//server
@Mapping(value = "/demoh/rpc", method = MethodType.SOCKET)
@Remoting
public class HelloRpcServiceImpl implements HelloRpcService {
    public String hello(String name) {
        //此处，可以根据 client session 创建一个连接 client 的 rpc service
        NameRpcService rpc = SocketD.create(Context.current(), NameRpcService.class);

        String name2 = rpc.name(name);

        return "name=" + name;
    }
}

//client
HelloRpcService rpc = SocketD.create("tcp://localhost:"+_port, HelloRpcService.class);

String rst = rpc.hello("noear");
```

* Solon cloud 配置服务使用
```java
@Controller
public class DemoController {
    public static void main(String[] args){
        Solon.start(DemoController.class,args);
    }
    
    //注入模式
    @CloudConfig(value = "user.name", autoRefreshed = true)
    String userName;
    
    @Mapping("/")
    public void run() {
        //手动模式
        userName = CloudClient.config().pull("user.name").value();
    }
}
```

* Solon cloud 事件总线使用
```java
//事件订阅与消费
@CloudEvent("hello.demo")
public class DemoEvent implements CloudEventHandler {
    @Override
    public boolean handler(Event event) throws Throwable {
        //返回成功
        return true;
    }
}

//事件产生
CloudClient.event().publish(new Event("hello.demo", msg));
```

* Solon cloud 分布式任务使用
```java
//注解模式 - Hander 风格
@CloudJob("JobHandlerDemo1")
public class JobHandlerDemo1 implements Handler {
    @Override
    public void handle(Context ctx) throws Throwable {
        //任务处理
    }
}

//手动模式
CloudClient.job().register("JobHandlerDemo3","",c->{
    //任务处理 
});
```


### Solon 框架详解

* [《Solon 框架详解（一）- 快速入门》](https://www.cnblogs.com/noear/p/14115763.html)
* [《Solon 框架详解（二）- Solon的核心》](https://www.cnblogs.com/noear/p/14115817.html)
* [《Solon 框架详解（三）- Solon的web与data开发》](https://www.cnblogs.com/noear/p/14115846.html)
* [《Solon 框架详解（四）- Solon的事务传播》](https://www.cnblogs.com/noear/p/14119759.html)
* [《Solon 框架详解（五）- Solon扩展机制之Solon Plugin》](https://www.cnblogs.com/noear/p/14125526.html)
* [《Solon 框架详解（六）- Solon的校验框架使用、定制与扩展》](https://www.cnblogs.com/noear/p/14128571.html)
* [《Solon 框架详解（七）- Solon Ioc 的注解对比Spring及JSR330》](https://www.cnblogs.com/noear/p/14139635.html)
* [《Solon 框架详解（八）- Solon的缓存框架使用和定制》](https://www.cnblogs.com/noear/p/14139720.html)
* [《Solon 框架详解（九）- 渲染控制之定制统一的接口输出》](https://www.cnblogs.com/noear/p/14139733.html)
* [《Solon 框架详解（十）- Solon 的常用配置》](https://www.cnblogs.com/noear/p/14139768.html)
* [《Solon 框架详解（十一）- Solon Cloud 的配置说明》](https://www.cnblogs.com/noear/p/14530716.html)

### Solon Aop 特色开发

* [《Solon Aop 特色开发（1）注入或手动获取配置》](https://www.cnblogs.com/noear/p/14801122.html)
* [《Solon Aop 特色开发（2）注入或手动获取Bean》](https://www.cnblogs.com/noear/p/14801154.html)
* [《Solon Aop 特色开发（3）构建一个Bean的三种方式》](https://www.cnblogs.com/noear/p/14801161.html)
* [《Solon Aop 特色开发（4）Bean 扫描的三种方式》](https://www.cnblogs.com/noear/p/14801167.html)
* [《Solon Aop 特色开发（5）切面与环绕拦截》](https://www.cnblogs.com/noear/p/14801197.html)
* [《Solon Aop 特色开发（6）新鲜货提取器，提取Bean的函数进行定制开发》](https://www.cnblogs.com/noear/p/14801249.html)

### 插件开发说明
* 新建一个 maven 项目
* 新建一个 java/{包名}/XPluginImp.java （implements XPlugin）
* 新建一个 resources/META-INF/solon/{包名.properties}
*    添加配置：solon.plugin={包名}.XPluginImp

### 启动顺序参考

* 1.实例化 Solon.global() 并加载配置
* 2.加载扩展文件夹
* 3.扫描插件并排序
* 4.运行 initialize 函数
* 5.推送 AppInitEndEvent [事件]
* 6.运行插件
* 7.推送 PluginLoadEndEvent [事件]
* 8.导入java bean(@Import)
* 9.扫描并加载java bean
* a.推送 BeanLoadEndEvent [事件]
* b.加载渲染印映关系
* c.执行bean加完成事件
* d.推送 AppLoadEndEvent [事件]
* e.结束


### Helloworld 的单机并发数 [《helloworld_wrk_test》](https://gitee.com/noear/helloworld_wrk_test)

> * 机器：2017 macbook pro 13, i7, 16g, MacOS 10.15, jdk11
> * 测试：wrk -t10 -c200 -d30s --latency "http://127.0.0.1:8080/"

|  solon 1.1.2 | 大小 | QPS | 
| -------- | -------- | -------- | 
| solon.boot.jlhttp(bio)     | 0.1m     | 4.7万左右     |
| solon.boot.jetty(nio, 支持servlet api)     | 1.8m     | 10.7万左右     | 
| solon.boot.undertow(nio, 支持servlet api)     | 4.2m     | 11.3万左右     | 
| solon.boot.smarthttp(aio)     | 0.3m     | 12.4万左右     | 


| spring boot 2.3.3  | 大小 |  QPS  | 
| -------- | -------- | -------- |
| spring-boot-starter-tomcat   | 16.1m |  3.2万左右  | 
| spring-boot-starter-jetty | 16m | 3.7万左右 |
| spring-boot-starter-undertow | 16.8m | 4.4万左右 |
