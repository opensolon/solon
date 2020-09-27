
[![Maven Central](https://img.shields.io/maven-central/v/org.noear/solon.svg)](https://mvnrepository.com/search?q=g:org.noear%20AND%20solon)

` QQ交流群：22200020 ` 

# solon for java

一个插件式微型Web框架。

支持jdk8+；主框架0.1mb；组合不同的插件应对不同需求；方便定制；快速开发。

* 采用Handler + Context 架构
* 自带IOC & AOP容器，支持MVC
* 支持Http（Serverlet 或 非Serverlet），WebSocket，Socket三种信号接入
* 插件可扩展可切换：启动插件，扩展插件，序列化插件，会话状态插件，视图插件(可共存) 等...



### Hello world：

```java
//Handler 模式：
public class App{
    public static void main(String[] args){
        XApp app = XApp.start(App.class,args);
        
        app.get("/",(c)->c.output("Hello world!"));
    }
}

//Controller 模式：
@XController
public class App{
    public static void main(String[] args){
        XApp.start(App.class,args);
    }
  
    @XMapping("/")
    public Object home(XContext c){
        return "Hello world!";  
    }
}
```


### 主框架与快速集成开发包：

###### 主框架

| 组件 | 说明 |
| --- | --- |
| org.noear:solon-parent | 框架版本管理 |
| org.noear:solon | 主框架 |

###### 快速集成开发包

| 组件 | 说明 |
| --- | --- |
| org.noear:solon-web | 可进行http api, mvc, rpc开发的快速集成包 |
| org.noear:fairy | 可做为solon rpc 的客户端（伴生项目） |



### 附1：入门示例
* Web 示例（mvc）
```xml
<parent>
    <groupId>org.noear</groupId>
    <artifactId>solon-parent</artifactId>
    <version>1.0.37</version>
</parent>

<dependencies>
    <dependency>
        <groupId>org.noear</groupId>
        <artifactId>solon-web</artifactId>
        <type>pom</type>
    </dependency>
</dependencies>

```
```
//资源路径说明（不用配置）
resources/application.properties（或 application.yml） 为应用配置文件
resources/static/ 为静态文件根目标
resources/WEB-INF/view/ 为视图文件根目标（支持多视图共存）

//调试模式：
启动参数添加：-deubg=1
```
```java
public class App{
    public static void main(String[] args){
        XApp.start(App.class, args);
    }
}

/*
 * mvc控制器
 */
@XController
public class DemoController{
    //for http
    @XMapping("/hallo/{u_u}")
    public ModelAndView hallo(String u_u){
        return new ModelAndView("hallo");
    }
    
    /*
    //for web socket （需添加：solon.boot.websocket 插件）
    @XMapping(value="/hallo/{u_u}", method = XMethod.SEND)
    public ModelAndView hallo_ws(String u_u){
        return new ModelAndView("hallo");
    }
    */
}
```

* Rpc 示例

```java
/*
 * rpc服务
 */ 
// - interface
@FairyClient("rpc:/demo/") // 或 demorpc （使用water提供的注册服务；当然也可以改成别的...）
public interface DemoService{
    void setName(Integer user_id, String name);
}

// - server
@XMapping("/demo/*")
@XBean(remoting = true)
public class DemoServiceImp implements DemoService{
    public void setName(Integer user_id, String name){
        
    }
}

// - client - 简单示例
//注入模式
//@FairyClient("http://127.0.0.1:8080/demo/") 
//DemoService client;

//构建模式
DemoService client = Fairy.builder().upstream(n->"http://127.0.0.1:8080").create(DemoService.class); 
client.setName(1,'');


```

* 获取应用配置
```java
//非注入模式
XApp.cfg().get("app_key"); //=>String
XApp.cfg().getInt("app_id",0); //=>int
XApp.cfg().getProp("xxx.datasource"); //=>Properties

//注入模式
@XConfiguration //or @XController, or @XBean
public class Config{
    @XInject("${app_key}")
    String app_key;
}
```

* 事务与缓存控制
```java
@XController
public class DemoController{
    @Db
    BaseMapper<UserModel> userService;
    
    @XCacheRemove(tags = "user_${user_id}")
    @XTran
    @XMapping("/user/update")
    public void udpUser(int user_id, UserModel user){
        userService.updateById(user);
    }
    
    @XCache(tags = "user_${user_id}")
    public UserModel getUser(int user_id){
        return userService.selectById(user_id);
    }
}
```

### 附2：示例参考
* 项目内的：[_test](./_test/) 和 [_demo](./_demo/)
* 更多示例：[solon_demo](https://gitee.com/noear/solon_demo)

### 附3：插件开发说明
* 新建一个 maven 项目
* 新建一个 java/{包名}/XPluginImp.java （implements XPlugin）
* 新建一个 resources/META-INF/solon/{包名.properties}
*    添加配置：solon.plugin={包名}.XPluginImp

### 附4：启动顺序参考
* 1.实例化 XApp.global() 并加载配置
* 2.加载扩展文件夹
* 3.扫描插件
* 4.运行builder函数
* 5.运行插件
* 6.扫描并加载java bean
* 7.加载渲染关系
* 8.完成

### 附5：Helloworld 的单机并发数

> 机器：2017款 macbook pro i7 16g
>
> 测试：wrk -t10 -c200 -d30s --latency "http://127.0.0.1:8080/"

|  solon  | 大小 | QPS | spring boot  |  QPS  | 
| -------- | -------- | -------- | -------- | -------- |
| solon.boot.jlhttp (可独立运行)     | 0.1m     | 4.5万左右     | /   |    | 
| /     |      |      | spring-boot-starter-tomcat   |  3.1万左右  | 
| solon.boot.jetty (可独立运行)     | 1.8m     | 6.5万左右     | spring-boot-starter-jetty | 3.5万左右 |
| solon.boot.undertow (可独立运行)     | 4.2m     | 7.9万左右     | spring-boot-starter-undertow | 4.9万左右 |
