
` QQ交流群：22200020 （同时招募项目参与人员：开发者，测试者，网官设计师等...）` 

# solon for java

### 插件式微型Web框架(主框架70kb，根据需求组合不同的插件或扩展；支持jdk8)

#### 框架实现效果
* 0.采用Handler + Context 基础架构（*类似spring webflux）
* 1.实现boot（*类似spring boot；可切换各种boot插件；支持http,websocket,socket）
* 2.实现微框架（*类似javalin）
* 3.实现mvc（*类似spring mvc；可支持多模板同存）
* 4.实现rpc（*类似dobbo）
* 5.实现微服务架构（结合water治理平台）//此项目不涉及
* 6.很强的可定制性（最近用solon搞了嵌入式FaaS引擎：https://github.com/noear/solonjt ）
#### 主要组成
* 1.XApp，管理总线，所有内容都交汇于此
* 2.XPlugin，通用插件接口（所以扩展都是它的实现）
* 3.XRender，通用渲染接口
* 4.XContext，通用上下文接口
* 5.XHandler，通用处理接口
* 6.XAction***，通用动作接口
* 7.XBean***，轻量级bean体系
#### 五个注解说明
注解主要分为：普通、特定、附助三类注解，实动时会被加载；原则上，只被应用在启动时。。。其它注解可借助lombok框架。
* 普通bean注解：<br/>
XBean (组件注解，可加注在类上)，remoting=true 时，开启rpc服务

* 特定web bean注解：需配置-parameters<br/>
XController（WEB控制器），加注@XMapping的公有函数为XAction<br/>
XInterceptor（WEB拦截器，支持多个排序），加注@XMapping的公有函数为XAction<br/> 

* 特定web bean 的附助注解：<br/>
XMapping：（映射注解，支持path var）。可注解到web bean或XAction或XHandler<br/>
XAfter：（后置解发器）。可注解到web bean或XAction<br/>
XBefore：（前置解发器）。可注解到web bean或XAction<br/>

#### 组件说明 <a href="https://search.maven.org/search?q=solon" target='_blank'>maven-central v1.0.3</a>

##### 主框架

| 组件 | 说明 |
| --- | --- |
| org.noear:solon-parent:1.0.3.20 | 框架版本管理 |
| org.noear:solon:1.0.3.20 | 70k，主框架 |

##### 插件

| boot插件 | 说明 |
| --- | --- |
| org.noear:solon.boot.jlhttp:1.0.3.6 | boot插件,对`jlhttp`适配,提供`http`服务（不自带session；可通过session插件提供支持） |
| org.noear:solon.boot.nteeyhttp:1.0.3.6 | boot插件,对`Netty`适配,提供`http`服务 |
| org.noear:solon.boot.jetty:1.0.3.6 | boot插件,对`jetty`适配,提供`http`服务 |
| org.noear:solon.boot.undertow:1.0.3.6 | boot插件,对`undertow`适配,提供`http`服务（网友@tyk提供） |
| org.noear:solon.boot.smarthttp:1.0.3.6 | boot插件,对`smart-http`适配,提供`http`服务（基于AIO实现） |
| org.noear:solon.boot.websocket:1.0.3.6 | boot插件,对`java-websocket`适配，提供`websocket`服务 |
| org.noear:solon.extend.jetty.jsp:1.0.3.2 | 扩展插件,为`jetty`添加`jsp`支持（不建议使用jsp）（网友@khb提供） |
| org.noear:solon.extend.undertow.jsp:1.0.3.2 | 扩展插件,为`undertow`添加`jsp`支持（不建议使用jsp）（网友@tyk提供） |
| org.noear:solon.boot.tomcat:1.0.3-b1 | boot插件,对`tomcat`适配,提供`http`服务 |
| org.noear:solon.boot.reactor-netty:1.0.3-b1 | boot插件,对`reactor-netty`适配,提供`http`服务 |

| 静态文件支持插件 | 说明 |
| --- | --- |
| org.noear:solon.extend.staticfiles:1.0.3.2 | 扩展插件,添加静态文件支持（监视 resources/static 文件夹） |

| Yaml配置支持插件 | 说明 |
| --- | --- |
| org.noear:solon.extend.properties.yaml:1.0.3.4 | 扩展插件,添加yml配置文件支持 |

| Session插件 | 说明（可将boot插件的session，自动换掉） |
| --- | --- |
| org.noear:solon.extend.sessionstate.local:1.0.3.3 | 扩展插件,本地`session` |
| org.noear:solon.extend.sessionstate.redis:1.0.3.3 | 扩展插件,分布式`session`（其于`redis`构建） |

| 序列化插件 | 说明 |
| --- | --- |
| org.noear:solon.serialization.fastjson:1.0.3.5 | 视图插件，对 `fastjson` 适配，输出`json`视图 或 序列化输出 |
| org.noear:solon.serialization.snack3:1.0.3.5 | 视图插件，对 `snack3` 适配，输出`json`视图 或 序列化输出 |
| org.noear:solon.serialization.jackson:1.0.3.5 | 视图插件，对 `jackson` 适配，输出`json`视图 或 序列化输出 |

| 视图插件 | 说明（可置多个视图插件） |
| --- | --- |
| org.noear:solon.view.freemarker:1.0.3.5 | 视图插件，对 `freemarker` 适配，输出`html` |
| org.noear:solon.view.jsp:1.0.3.5 | 视图插件，对 `jsp` 适配，输出`html` |
| org.noear:solon.view.velocity:1.0.3.5 | 视图插件，对 `velocity` 适配，输出`html` |
| org.noear:solon.view.thymeleaf:1.0.3.5 | 视图插件，对 `thymeleaf` 适配，输出`html` |
| org.noear:solon.view.beetl:1.0.3.5 | 视图插件，对 `beetl` 适配，输出`html` |
| org.noear:solon.view.enjoy:1.0.3.5 | 视图插件，对 `enjoy` 适配，输出`html` |

| rpc client | 说明 |
| --- | --- |
| org.noear:solonclient:1.0.3.2 | 11k，`rpc` client 框架，与solon 的 rpc service 配对 |

#### 简单示例
* 微框架示例
```xml
<parent>
    <groupId>org.noear</groupId>
    <artifactId>solon-parent</artifactId>
    <version>1.0.3.19</version>
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
    <version>1.0.3.19</version>
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
