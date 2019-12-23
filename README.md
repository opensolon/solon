
[![Maven Central](https://img.shields.io/maven-central/v/org.noear/solon.svg)](https://mvnrepository.com/search?q=solon)

` QQ交流群：22200020 （同时招募项目参与人员：开发者，测试者，网官设计师等...）` 

# solon for java

一个插件式微型Web框架。

支持jdk8，主框架80kb。组合不同的插件应对不同需求。



* Handler + Context 架构
* 控制器 + 拦截器 + 触发器 + 渲染器
* 插件扩展：启动插件 + 扩展插件 + 序列化插件 + 视图插件(可共存) + 等...

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


### 主框架与插件：

###### 主框架

| 组件 | 说明 |
| --- | --- |
| org.noear:solon-parent | 框架版本管理 |
| org.noear:solon | 主框架 |

###### 快速集成包

| 组件 | 说明 |
| --- | --- |
| org.noear:solon-mvc | 可进行mvc开发的快速集成包 |
| org.noear:solon-api | 可进行api 或 rpc 开发的快速集成包 |

###### 插件

| boot插件 | 说明 |
| --- | --- |
| org.noear:solon.boot.jdkhttp | boot插件,对`JKD`自带的`HttpServer`适配,提供`http`服务（不自带session state） |
| org.noear:solon.boot.jlhttp | boot插件,对`jlhttp`适配,提供`http`服务（不自带session state） |
| org.noear:solon.boot.nteeyhttp | boot插件,对`Netty`适配,提供`http`服务 |
| org.noear:solon.boot.jetty | boot插件,对`jetty`适配,提供`http`服务（网友@khb提供） |
| org.noear:solon.boot.undertow | boot插件,对`undertow`适配,提供`http`服务（网友@tyk提供） |
| org.noear:solon.boot.smarthttp | boot插件,对`smart-http`适配,提供`http`服务（基于AIO实现） |
| org.noear:solon.boot.websocket | boot插件,对`java-websocket`适配，提供`websocket`服务 |
| org.noear:solon.extend.jetty.jsp | 扩展插件,为`jetty`添加`jsp`支持（不建议使用jsp）（网友@khb提供） |
| org.noear:solon.extend.undertow.jsp | 扩展插件,为`undertow`添加`jsp`支持（不建议使用jsp）（网友@tyk提供） |

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
| org.noear:solonclient | solon rpc client 与solon 的 rpc service 配对 |

### 附1：入门示例
* 微框架示例
```xml
<!-- http boot 插件；可以换成：.smarthttp 或 .jetty 或 .undertow 或自己定义个 -->
<dependency>
  <groupId>org.noear</groupId>
  <artifactId>solon.boot.jlhttp</artifactId>
  <version>1.0.4</version>
</dependency>
```
```java
public class App{
    public static void main(String[] args){
        XApp app = XApp.start(App.class,args);
        
        //http get 监听
        app.get("/",(c)->c.output("hallo world!"));
    }
}
```
* Web 示例（aop,mvc,rpc）
```xml
<parent>
    <groupId>org.noear</groupId>
    <artifactId>solon-mvc</artifactId>
    <version>1.0.4</version>
</parent>
```
```
//资源路径说明（不用配置）
resources/application.properties（或 application.yml） 为应用配置文件
resources/static/ 为静态文件根目标
resources/WEB-INF/view/ 为视图文件根目标（支持多视图共存）

//模板调试模式（或加热加载模式）：
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

/*
 * rpc服务
 */ 
// - interface
@XClient("rpc:/demo/") // 或 demorpc （使用water提供的注册服务；当然也可以改成别的...）
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
DemoRpc client = new XProxy().upstream(n->"http://127.0.0.1").create(DemoRpc.class); 
client.setName(1,'');
```
* 获取应用配置
```java
XApp.cfg().get("app_key"); //=>String
XApp.cfg().getInt("app_id",0); //=>int
XApp.cfg().getProp("xxx.datasource"); //=>Properties
```

### 附2：更多示例可参考 _test 和 _demo

### 附3：插件开发说明
* 新建一个 meven 项目
* 新建一个 java/{包名}/XPluginImp.java （implements XPlugin）
* 新建一个 resources/`solonplugin`/{包名.properties}
*    添加配置：solon.plugin={包名}.XPluginImp
