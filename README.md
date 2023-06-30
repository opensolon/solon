<h1 align="center" style="text-align:center;">
<img src="solon_icon.png" width="128" />
<br />
Solon v2.3.7
</h1>
<p align="center">
	<strong>Java 新的生态型应用开发框架，更小、更快、更简单！</strong>
</p>
<p align="center">
	<a href="https://solon.noear.org/">https://solon.noear.org</a>
</p>

<p align="center">
    <a target="_blank" href="https://central.sonatype.com/search?q=org.noear%2520solon-parent">
        <img src="https://img.shields.io/maven-central/v/org.noear/solon.svg?label=Maven%20Central" alt="Maven" />
    </a>
    <a target="_blank" href="https://www.apache.org/licenses/LICENSE-2.0.txt">
		<img src="https://img.shields.io/:License-Apache2-blue.svg" alt="Apache 2" />
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
    <a target="_blank" href="https://www.oracle.com/java/technologies/javase/jdk20-archive-downloads.html">
		<img src="https://img.shields.io/badge/JDK-20-green.svg" alt="jdk-20" />
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

##### 语言： 中文 | [English](README_EN.md) | [Русский](README_RU.md) | [日本語](README_JP.md)

<hr />

启动快 5 ～ 10 倍；qps 高 2～ 3 倍；运行时内存节省 1/3 ~ 1/2；打包可以缩到 1/2 ~ 1/10

<hr />

## 介绍：

从零开始构建。有自己的标准规范与开放生态。组合不同的生态插件应对不同需求，方便定制，快速开发：

* **克制、简洁、高效、开放、生态**
* 支持 JDK8、JDK11、JDK17、JDK20
* Http、WebSocket、Socket 三种信号统一的开发体验（俗称：三源合一）
* 支持“注解”与“手动”两种模式，按需自由操控
* Not Servlet，可以适配任何基础通讯框架（最小 0.3m 运行rpc架构）
* 独特的 IOC/AOP 容器设计。不会因为插件变多而启动变很慢
* 支持 Web、Data、Job、Remoting、Cloud 等任何开发场景
* 兼顾 Handler + Context 和 Listener + Message 两种架构模式
* 强调插件式扩展，可扩展可切换；适应不同的应用场景
* 支持 GraalVm Native 打包
* 允许业务插件“热插”、“热拔”、“热管理”


## 生态架构图：

<img src="solon_schema.png" width="700" />

## 你好世界：

```xml
<parent>
    <groupId>org.noear</groupId>
    <artifactId>solon-parent</artifactId>
    <version>2.3.7</version>   
</parent>

<dependencies>
    <dependency>
        <groupId>org.noear</groupId>
        <artifactId>solon-web</artifactId>
    </dependency>
</dependencies>
```

```java
@SolonMain
public class App{
    public static void main(String[] args){
        Solon.start(App.class, args, app->{
            //Handler 模式：
            app.get("/hello",(c)->c.output("Hello world!"));
        });
    }
}

//Controller 模式：(mvc or rest-api)
@Controller
public class HelloController{
    //限定 Socket 方法类型
    @Socket
    @Mapping("/mvc/hello")
    public String hello(String name){
        return "Hello " + name;
    }
}

//Remoting 模式：(rpc)
@Mapping("/rpc/")
@Remoting
public class HelloServiceImpl implements HelloService{
    @Override
    public String hello(){
        return "Hello world!";
    }
}
```


## 主框架及快速集成开发包：

###### 主框架：

| 组件包                    | 说明                          |
|------------------------|-----------------------------|
| org.noear:solon-parent | 依赖版本管理                      |
| org.noear:solon        | 主框架                         |
| org.noear:nami         | 伴生框架（作为solon remoting 的客户端） |

###### 快速集成开发包及相互关系：

| 组件包                       | 说明                                                    |
|---------------------------|-------------------------------------------------------|
| org.noear:solon-lib       | 快速开发基础集成包                                             |
| org.noear:solon-api       | solon-lib + jlhttp boot；快速开发接口应用                       |
| org.noear:solon-web       | solon-api + freemarker + sessionstate；快速开发WEB应用       |
| org.noear:solon-beetl-web | solon-api + beetl + beetlsql + sessionstate；快速开发WEB应用 |
| org.noear:solon-enjoy-web | solon-api + enjoy + arp + sessionstate；快速开发WEB应用      |
| org.noear:solon-rpc       | solon-api + nami；快速开发RPC应用                            |
| org.noear:solon-cloud     | solon-rpc + consul；快速开发微服务应用                          |


## 加入到交流群：

| QQ交流群：22200020                       | 微信交流群（申请时输入：Solon）                     |
|---------------------------|----------------------------------------|
| <img src="group_qq.png" width="120" />       | <img src="group_wx.png" width="120" /> 


交流群里，会提供 "保姆级" 支持和帮助。如有需要，也可提供技术培训和顾问服务

## 官网及相关示例：

* 官网地址：[https://solon.noear.org](https://solon.noear.org)
* 官网配套演示：[https://gitee.com/noear/solon-examples](https://gitee.com/noear/solon-examples)
* 项目单测：[__test](./__test/) 
* 项目更多功能示例：[solon_api_demo](https://gitee.com/noear/solon_api_demo)  、 [solon_rpc_demo](https://gitee.com/noear/solon_rpc_demo) 、 [solon_socketd_demo](https://gitee.com/noear/solon_socketd_demo) 、 [solon_cloud_demo](https://gitee.com/noear/solon_cloud_demo) 、 [solon_auth_demo](https://gitee.com/noear/solon_auth_demo)

## 特别感谢JetBrains对开源项目支持：

<a href="https://jb.gg/OpenSourceSupport">
  <img src="https://user-images.githubusercontent.com/8643542/160519107-199319dc-e1cf-4079-94b7-01b6b8d23aa6.png" align="left" height="100" width="100"  alt="JetBrains">
</a>

