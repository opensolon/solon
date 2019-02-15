还没有上传中央仓库，外人还不方便用。（不会搞，得找人请教一下...）
<br/>
源码中的小部分扩展未搞完。

### 插件式Web微框架(主框架50kb，根据需求组合不同的插件或扩展)
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
* Web 示例（aop,mvc,rpc）
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
