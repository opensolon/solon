### 插件式Web微框架(主框架40kb)
#### 框架实现效果
* 1.实现boot（*类似spring boot）
* 2.实现微框架（*类似javalin）
* 3.实现mvc（*类似spring mvc）
* 4.实现rpc
* 5.实现微服务架构（结合water治理平台）
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
#### 简单示例
* 微框架示例
```java
public class App{
    public static void main(String[] args){
        XApp app = XApp.start(App.class,args);
        app.get("/hallo",(c)->c.output("hallo world!"));
    }
}
```
* Web bean示例
```java
public class App{
    public static void main(String[] args){
        XApp.start(App.class,args);
    }
}

/*mvc控制器*/
@XController
public class DemoController{
    @XMappinge("/hallo/{u_u}")
    public ModelAndView hallo(String u_u){
        return new ModelAndView("hallo");
    }
}

/*rpc服务*/ 
// - interface
@XClient("demorpc,http://127.0.0.1/demo/")
public interface DemoRpc{
    void setName(Integer user_id,String name);
}

// - server
@XMappinge("/demo/*")
@XService
public class DemoService implements DemoRpc{
    public void setName(Integer user_id,String name){
        
    }
}

// - client - 简单示例
DemoRpc client = new XProxy().create(DemoRpc.class);
client.setName(1,'');

// - client - 使用WATER负载示例
DemoRpc client = XWaterUpstream.xclient(DemoRpc.class);
client.setName(1,'');
```
