<h1 align="center" style="text-align:center;">
<img src="solon_icon.png" width="128" />
<br />
Solon v2.2.6-SNAPSHOT
</h1>
<p align="center">
	<strong>An efficient Java application development framework, smaller, faster, simpler!</strong>
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

##### Language: English | [中文](README.md)

<hr />

Start 5 ~ 10 times faster; QPS 2 ~ 3 times higher; runtime memory savings of 1/3 ~ 1/2; packaging can be reduced by 1/2 ~ 1/10

<hr />

## Introduction:

**Solon** is an efficient Java application development framework. It also has a rich open ecosystem of plugins, combining different plugins to meet various needs, enabling convenient customization and rapid development.


* **Restraint, simplicity, openness, and ecology**
* Supports JDK8, JDK11, JDK17, and JDK19
* Unified development experience for HTTP, WebSocket, and Socket signals
* Supports both "annotation" and "manual" modes, allowing for free control as needed
* Not Servlet-based, can be adapted to any basic communication framework (minimum 0.3m running RPC architecture)
* Built-in IOC/AOP container, supporting Web, Data, Job, Remoting, Cloud, and other development scenarios
* Two architecture modes: Handler + Context and Listener + Message
* Emphasizes plugin extension, which can be extended and switched to adapt to different application scenarios
* Allows service plugins to be hot-inserted or hot-dialed
* Supports GraalVM Native packaging
* Not Spring, not Servlets, not JavaEE; a newly independent open ecosystem


## Ecological architecture map:

<img src="solon_schema.png" width="700" />

## Hello world：

```xml
<parent>
    <groupId>org.noear</groupId>
    <artifactId>solon-parent</artifactId>
    <version>2.2.6-SNAPSHOT</version>   
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


## Main framework and rapid integration development kit：

###### Main framework:

| Component package | description |
| --- | --- |
| org.noear:solon-parent | Dependent version management |
| org.noear:solon | Main framework |
| org.noear:nami | Companion framework (as a client for solon remoting)|

###### Rapid integration of development kits and interrelationships:

| Component package | description                                                                  |
| --- |------------------------------------------------------------------------------|
| org.noear:solon-lib | Rapid development of basic integration packages                              |
| org.noear:solon-api | solon-lib + jlhttp boot；Develop api applications quickly                     |
| org.noear:solon-web | solon-api + freemarker + sessionstate；Develop web applications quickly       |
| org.noear:solon-beetl-web | solon-api + beetl + beetlsql + sessionstate；Develop web applications quickly |
| org.noear:solon-enjoy-web | solon-api + enjoy + arp + sessionstate；Develop web applications quickly      |
| org.noear:solon-rpc | solon-api + nami；Develop rpc applications quickly                            |
| org.noear:solon-cloud | solon-rpc + consul；Develop cloud applications quickly                        |


## Official website and related examples:

* Official website address: [https://solon.noear.org](https://solon.noear.org)
* Official website supporting demonstration: [https://gitee.com/noear/solon-examples](https://gitee.com/noear/solon-examples)
* Project unit test: [__test](./__test/) 
* Example of more functions for the project: [solon_demo](https://gitee.com/noear/solon_demo) 、 [solon_api_demo](https://gitee.com/noear/solon_api_demo)  、 [solon_rpc_demo](https://gitee.com/noear/solon_rpc_demo) 、 [solon_socketd_demo](https://gitee.com/noear/solon_socketd_demo) 、 [solon_cloud_demo](https://gitee.com/noear/solon_cloud_demo) 、 [solon_auth_demo](https://gitee.com/noear/solon_auth_demo)

## Special thanks to JetBrains for supporting the open source project:

<a href="https://jb.gg/OpenSourceSupport">
  <img src="https://user-images.githubusercontent.com/8643542/160519107-199319dc-e1cf-4079-94b7-01b6b8d23aa6.png" align="left" height="100" width="100"  alt="JetBrains">
</a>

