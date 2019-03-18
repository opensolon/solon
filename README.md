
### 插件式Web微框架(主框架50kb，根据需求组合不同的插件或扩展)
#### 框架实现效果
* 1.实现boot（*类似spring boot）
* 2.实现微框架（*类似javalin）
* 3.实现mvc（*类似spring mvc）
* 4.实现rpc
* 5.实现微服务架构（结合water治理平台）//此项目不涉及此事
#### 主要组成
* 1.XApp，管理总线，所有内容都交汇于此
* 2.XPlugin，通用插件接口
* 3.XRender，通用渲染接口
* 4.XContext，通用上下文接口
* 5.XHandler，通用处理接口
* 6.XAction***，通用动作接口（XHandler+XRender）
* 7.XBean***，轻量级bean体系
#### 五个注解说明
注解主要分为：普通、特定、附助三类注解，实动时会被加载；原则上，只被应用在启动时。。。其它注解可借助lombok框架。
* 普通bean注解：XBean，令可加注在类上
* 特定web bean注解：需配置-parameters<br/>
XController（WEB控制者），加注@XMapping的公有函数为XAction<br/>
XInterceptor（WEB拦截者），加注@XMapping的公有函数为XAction<br/> 
XService（WEB服务者），所有公有函数为XAction<br/>
* 特定web bean 的附助注解：<br/>
XMapping：印射注解，支持path var。可注解到web bean或XAction或XHandler<br/>
XAfter：后置处理注解。可注解到web bean或XAction<br/>
XBefore：前置处理注解。可注解到web bean或XAction<br/>

#### 组件说明
| 组件 | 说明 |
| --- | --- |
| org.noear:solon:1.0.2 | 主框架 |
| org.noear:solon.boot.jlhttp:1.0.2 | boot插件,对jlhttp适配（不支持session） |
| org.noear:solon.boot.jetty:1.0.2 | boot插件,对jetty适配 |
| org.noear:solon.extend.jetty.jsp:1.0.2 | 扩展插件,为jetty添加jsp支持 |
| org.noear:solon.extend.staticfiles:1.0.2 | 扩展插件,添加静态文件支持 |
| org.noear:solon.view.fastjson:1.0.2 | 视图插件，对 fastjson 适配，输出json视图 |
| org.noear:solon.view.freemarker:1.0.2 | 视图插件，对 fastjson 和 freemarker 适配，输出json或html |
| org.noear:solon.view.jsp:1.0.2 | 视图插件，对 fastjson 和 jsp 适配，输出json或html |
| org.noear:solon.view.velocity:1.0.2 | 视图插件，对 fastjson 和 velocity 适配，输出json或html |
| org.noear:solonclient:1.0.2 | rpc client 框架，与solon 的 rpc service 配对 |

#### 简单示例
* 微框架示例
```xml
<dependency>
  <groupId>org.noear</groupId>
  <artifactId>solon</artifactId>
  <version>1.0.2</version>
</dependency>

<dependency>
  <groupId>org.noear</groupId>
  <artifactId>solon.boot.jlhttp</artifactId><!-- 可以换成：.jetty 或自己定义个插件 -->
  <version>1.0.2</version>
</dependency>
```
```java
public class App{
    public static void main(String[] args){
        XApp app = XApp.start(App.class,args);
        app.get("/hallo",(c)->c.output("hallo world!"));
    }
}
```
* Web 示例（aop,mvc,rpc）
```xml
<dependency>
  <groupId>org.noear</groupId>
  <artifactId>solon</artifactId>
  <version>1.0.2</version>
</dependency>

<dependency>
  <groupId>org.noear</groupId>
  <artifactId>solon.boot.jlhttp</artifactId> <!-- 可以换成：.jetty 或自己定义个插件 -->
  <version>1.0.2</version>
</dependency>

<dependency>
  <groupId>org.noear</groupId>
  <artifactId>solon.view.freemarker</artifactId> <!-- 可以换成：.velocity 或 .jsp 或自己定义个插件 -->
  <version>1.0.2</version>
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
    @XMapping("/hallo/{u_u}")
    public ModelAndView hallo(String u_u){
        return new ModelAndView("hallo");
    }
}

/*rpc服务*/ 
// - interface
@XClient("http://127.0.0.1/demo/") // 或 demorpc （使用water提供的注册服务；当然也可以改成别的...）
public interface DemoRpc{
    void setName(Integer user_id,String name);
}

// - server
@XMapping("/demo/*")
@XService(remoting = true)
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
