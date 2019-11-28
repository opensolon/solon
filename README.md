
[![Maven Central](https://img.shields.io/maven-central/v/org.noear/solon.svg)](https://search.maven.org/search?q=solon)

` QQ交流群：22200020 （同时招募项目参与人员：开发者，测试者，网官设计师等...）` 

# solon for java

### 插件式微型Web框架，主框架70kb，支持jdk8。

### 组合不同的插件应对不同需求。



#### 三组概念组成整体架构

* Handler + Context 架构
* 控制器 + 拦截器 + 触发器 + 渲染器
* 插件：启动插件 + 扩展插件 + 序列化插件 + 视图插件



#### 架构效果

- 0.实现微框架（*类似`javalin`）
- 1.实现boot（*类似`spring boot`；可切换各种boot插件；支持`http`, `websocket`, `socket`）
- 2.实现mvc（*类似`spring mvc`；可支持多模板同存）
- 3.实现rpc（*类似`dobbo`）
- 4.实现微服务架构（结合water治理平台）



#### Hello world

```java
public class App{
    public static void main(String[] args){
        XApp app = XApp.start(App.class,args);
        
        app.get("/",(c)->c.output("Hello world!"));
    }
}

```



#### 框架与插件

##### 主框架

| 组件 | 说明 |
| --- | --- |
| org.noear:solon-parent | 框架版本管理 |
| org.noear:solon | 70k，主框架 |
| org.noear:solon-mvc | 可进行mvc开发的快速集成包 |
| org.noear:solon-api | 可进行api 或 rpc 开发的快速集成包 |

##### 插件

| boot插件 | 说明 |
| --- | --- |
| org.noear:solon.boot.jlhttp | boot插件,对`jlhttp`适配,提供`http`服务（不自带session state） |
| org.noear:solon.boot.nteeyhttp | boot插件,对`Netty`适配,提供`http`服务 |
| org.noear:solon.boot.jetty | boot插件,对`jetty`适配,提供`http`服务（网友@khb提供） |
| org.noear:solon.boot.undertow | boot插件,对`undertow`适配,提供`http`服务（网友@tyk提供） |
| org.noear:solon.boot.smarthttp | boot插件,对`smart-http`适配,提供`http`服务（基于AIO实现） |
| org.noear:solon.boot.websocket | boot插件,对`java-websocket`适配，提供`websocket`服务 |
| org.noear:solon.extend.jetty.jsp | 扩展插件,为`jetty`添加`jsp`支持（不建议使用jsp）（网友@khb提供） |
| org.noear:solon.extend.undertow.jsp | 扩展插件,为`undertow`添加`jsp`支持（不建议使用jsp）（网友@tyk提供） |
| | |
| org.noear:solon.boot.tomcat:beta | boot插件,对`tomcat`适配,提供`http`服务 |
| org.noear:solon.boot.reactor-netty:beta | boot插件,对`reactor-netty`适配,提供`http`服务 |

| 静态文件支持插件 | 说明 |
| --- | --- |
| org.noear:solon.extend.staticfiles | 扩展插件,添加静态文件支持（监视 resources/static 文件夹） |

| Yaml配置支持插件 | 说明 |
| --- | --- |
| org.noear:solon.extend.properties.yaml | 扩展插件,添加yml配置文件支持 |

| Session插件 | 说明（可将boot插件的session state服务，自动换掉） |
| --- | --- |
| org.noear:solon.extend.sessionstate.local | 扩展插件,本地`session` |
| org.noear:solon.extend.sessionstate.redis | 扩展插件,分布式`session`（其于`redis`构建） |

| 序列化插件 | 说明 |
| --- | --- |
| org.noear:solon.serialization.fastjson | 视图插件，对 `fastjson` 适配，输出`json`视图 或 序列化输出 |
| org.noear:solon.serialization.snack3 | 视图插件，对 `snack3` 适配，输出`json`视图 或 序列化输出 |
| org.noear:solon.serialization.jackson | 视图插件，对 `jackson` 适配，输出`json`视图 或 序列化输出 |

| 视图插件 | 说明（可置多个视图插件） |
| --- | --- |
| org.noear:solon.view.freemarker | 视图插件，对 `freemarker` 适配，输出`html` |
| org.noear:solon.view.jsp | 视图插件，对 `jsp` 适配，输出`html` |
| org.noear:solon.view.velocity | 视图插件，对 `velocity` 适配，输出`html` |
| org.noear:solon.view.thymeleaf | 视图插件，对 `thymeleaf` 适配，输出`html` |
| org.noear:solon.view.beetl | 视图插件，对 `beetl` 适配，输出`html` |
| org.noear:solon.view.enjoy | 视图插件，对 `enjoy` 适配，输出`html` |

| rpc client | 说明 |
| --- | --- |
| org.noear:solonclient | 11k，`rpc` client 框架，与solon 的 rpc service 配对 |

#### 简单示例
* 微框架示例
```xml
<parent>
    <groupId>org.noear</groupId>
    <artifactId>solon-parent</artifactId>
    <version>1.0.3.30</version>
</parent>

<dependency>
  <groupId>org.noear</groupId>
  <artifactId>solon</artifactId>
</dependency>

<!-- http boot 插件；可以换成：.smarthttp 或 .jetty 或 .undertow 或自己定义个 -->
<dependency>
  <groupId>org.noear</groupId>
  <artifactId>solon.boot.jlhttp</artifactId>
</dependency>
```
```java
public class App{
    public static void main(String[] args){
        XApp app = XApp.start(App.class,args);
        
        //http get 监听
        app.get("/hallo_http",(c)->c.output("hallo world!"));
        
        //web socket send 监听（需添加：solon.boot.websocket 插件）
        //app.send("/hello_ws",(c)->c.output("hallo world!"));
    }
}
```
* Web 示例（aop,mvc,rpc）
```xml
<parent>
    <groupId>org.noear</groupId>
    <artifactId>solon-parent</artifactId>
    <version>1.0.3.30</version>
</parent>

<dependency>
  <groupId>org.noear</groupId>
  <artifactId>solon</artifactId>
</dependency>

<dependency>
  <groupId>org.noear</groupId>
  <artifactId>solon.boot.jlhttp</artifactId> <!-- 可以换成：.jetty 或自己定义个插件 -->
</dependency>

<dependency>
    <groupId>org.noear</groupId>
    <artifactId>solon.serialization.fastjson</artifactId>
</dependency>

<dependency>
  <groupId>org.noear</groupId>
  <artifactId>solon.view.freemarker</artifactId> <!-- 可以换成：.velocity 或 .jsp 或自己定义个插件 -->
</dependency>
```
```
//资源路径说明
resources/application.properties 为应用配置文件
resources/static/ 为静态文件根目标
resources/WEB-INF/view/ 为视图文件根目标 （把视图放数据库里也成...自己适配下）
```
```java
public class App{
    public static void main(String[] args){
        XApp.start(App.class,args);
    }
}

/*mvc控制器*/
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

/*rpc服务*/ 
// - interface
@XClient("http://127.0.0.1/demo/") // 或 demorpc （使用water提供的注册服务；当然也可以改成别的...）
public interface DemoRpc{
    void setName(Integer user_id,String name);
}

// - server
@XMapping("/demo/*")
@XB(remoting = true)
public class DemoService implements DemoRpc{
    public void setName(Integer user_id,String name){
        
    }
}

// - client - 简单示例
DemoRpc client = new XProxy().create(DemoRpc.class); //@XClient("http://127.0.0.1/demo/")
client.setName(1,'');

// - client - 使用WATER负载示例 （可以自己写个别的负载）
DemoRpc client = XWaterUpstream.xclient(DemoRpc.class); //@XClient("demorpc")
client.setName(1,'');
```
* 获取应用配置
```java
Aop.prop().get("app_key"); //=>String
Aop.prop().getInt("app_id",0); //=>int
Aop.prop().getProp("xxx.datasource"); //=>Properties
```

### 插件开发说明
* 新建一个 meven 项目
* 新建一个 java/{包名}/{插件类}.java （implements XPlugin）
* 新建一个 resources/`solonplugin`/{包名.properties}
*    添加配置：solon.plugin={包名}.{插件类}
