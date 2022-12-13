<h1 align="center" style="text-align:center;">
<img src="icon.png" width="128" />
<br />
Solon v1.11.6-M1
</h1>
<p align="center">
	<strong>更现代感的，轻量级应用开发框架</strong>
</p>
<p align="center">
	<a href="https://solon.noear.org/">https://solon.noear.org</a>
</p>

<p align="center">
    <a target="_blank" href="https://search.maven.org/search?q=org.noear%20solon">
        <img src="https://img.shields.io/maven-central/v/org.noear/solon.svg?label=Maven%20Central" alt="Maven" />
    </a>
    <a target="_blank" href="https://www.apache.org/licenses/LICENSE-2.0.txt">
		<img src="https://img.shields.io/:license-Apache2-blue.svg" alt="Apache 2" />
	</a>
    <a target="_blank" href="https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html">
		<img src="https://img.shields.io/badge/JDK-8-green.svg" alt="jdk-8" />
	</a>
    <a target="_blank" href="https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html">
		<img src="https://img.shields.io/badge/JDK-11-green.svg" alt="jdk-11" />
	</a>
    <a target="_blank" href="https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html">
		<img src="https://img.shields.io/badge/JDK-17-green.svg" alt="jdk-17" />
	</a>
    <a target="_blank" href="https://www.oracle.com/java/technologies/javase/jdk19-archive-downloads.html">
		<img src="https://img.shields.io/badge/JDK-19-green.svg" alt="jdk-19" />
	</a>
    <br />
    <a target="_blank" href='https://gitee.com/noear/solon/stargazers'>
		<img src='https://gitee.com/noear/solon/badge/star.svg' alt='gitee star'/>
	</a>
    <a target="_blank" href='https://github.com/noear/solon/stargazers'>
		<img src="https://img.shields.io/github/stars/noear/solon.svg?logo=github" alt="github star"/>
	</a>
</p>

<br/>
<p align="center">
	<a href="https://jq.qq.com/?_wv=1027&k=kjB5JNiC">
	<img src="https://img.shields.io/badge/QQ交流群-22200020-orange"/></a>
</p>


<hr />

启动快 5 ～ 10 倍；qps 高 2～ 3 倍；运行时内存节省 1/3 ~ 1/2；打包可以缩到 1/2 ~ 1/10

<hr />

## Solon

更现代感的应用开发框架。**更快、更小、更少、更自由！**

支持jdk8、jdk11、jdk17、jdk19；主框架0.1mb；组合不同的插件应对不同需求；方便定制；快速开发。

* 克制、简洁、开放、生态
* Http、WebSocket、Socket 三种信号统一的开发体验（俗称：三源合一）
* 支持注解与手动两种模式，按需自由操控
* Not Servlet，可以适配任何基础通讯框架（所以：最小0.2m运行rpc架构）
* 自建 IOC & AOP容器，支持 Web、Data、Job、Remoting、Cloud 等任何开发场景
* 集合 Handler + Context 和 Listener + Message 两种架构模式；强调插件式扩展；适应不同的应用场景
* 插件可扩展可切换：启动插件，扩展插件，序列化插件，数据插件，会话状态插件，视图插件(可共存) 等...
* 支持 GraalVm Native 打包
* 允许 业务插件 热插、热拨
* 没有 Spring，没有 Servlet，没有 JavaEE；独立的轻量生态


## Solon Cloud

一系列分布式开发的接口标准和配置规范，相当于DDD模式里的防腐层概念。是 Solon 的微服务架构模式开发解决方案。
目前已适配了一系列的插件用于支持这一标准：[《Solon Cloud 分布式服务开发套件清单，感觉受与 Spring Cloud 的不同》](https://my.oschina.net/noear/blog/5039169)

其中，[Water 项目](https://gitee.com/noear/water) 是一站式支持 Solon Cloud 系列标准的支撑平台。
功能相当于：consul + rabbitmq + elk + prometheus + openFaas + quartz + 等等，并有机结合在一起。一直与 Solon 项目伴生成长。



## Hello world：

```xml
<parent>
    <groupId>org.noear</groupId>
    <artifactId>solon-parent</artifactId>
    <version>1.11.6-M1</version>
</parent>

<dependencies>
    <dependency>
        <groupId>org.noear</groupId>
        <artifactId>solon-web</artifactId>
    </dependency>
</dependencies>
```

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
  
    //限定 Socket 方法类型
    @Socket
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


## 主框架及快速集成开发包：

###### 主框架

| 组件 | 说明 |
| --- | --- |
| org.noear:solon-parent | 框架版本管理 |
| org.noear:solon | 主框架 |
| org.noear:nami | 伴生框架（做为solon remoting 的客户端）|

###### 快速集成开发包及相互关系

| 组件 | 说明                                                    |
| --- |-------------------------------------------------------|
| org.noear:solon-lib | 快速开发基础集成包                                             |
| org.noear:solon-api | solon-lib + jlhttp boot；快速开发接口应用                       |
| org.noear:solon-web | solon-api + freemarker + sessionstate；快速开发WEB应用       |
| org.noear:solon-beetl-web | solon-api + beetl + beetlsql + sessionstate；快速开发WEB应用 |
| org.noear:solon-enjoy-web | solon-api + enjoy + arp + sessionstate；快速开发WEB应用      |
| org.noear:solon-rpc | solon-api + nami；快速开发RPC应用                            |
| org.noear:solon-cloud | solon-rpc + consul；快速开发微服务应用                          |

## 快速了解 Solon 架构的材料：

##### [《Solon 的想法与架构笔记》](https://my.oschina.net/noear/blog/4980834)
##### [《Solon 生态插件清单》](https://my.oschina.net/noear/blog/5053423)

## 官网及相关示例：

* 官网地址：[https://solon.noear.org](https://solon.noear.org)
* 官网配套演示：[https://gitee.com/noear/solon-examples](https://gitee.com/noear/solon-examples)
* 项目单测：[__test](./__test/) 
* 项目更多功能示例：[solon_demo](https://gitee.com/noear/solon_demo) 、 [solon_api_demo](https://gitee.com/noear/solon_api_demo)  、 [solon_rpc_demo](https://gitee.com/noear/solon_rpc_demo) 、 [solon_socketd_demo](https://gitee.com/noear/solon_socketd_demo) 、 [solon_cloud_demo](https://gitee.com/noear/solon_cloud_demo) 、 [solon_auth_demo](https://gitee.com/noear/solon_auth_demo)

## 特别感谢JetBrains对开源项目支持
<a href="https://jb.gg/OpenSourceSupport">
  <img src="https://user-images.githubusercontent.com/8643542/160519107-199319dc-e1cf-4079-94b7-01b6b8d23aa6.png" align="left" height="100" width="100"  alt="JetBrains">
</a>

