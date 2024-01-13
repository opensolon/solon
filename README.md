<h1 align="center" style="text-align:center;">
<img src="solon_icon.png" width="128" />
<br />
Solon v2.6.6-SNAPSHOT
</h1>
<p align="center">
	<strong>Java's new application development framework, smaller, faster, and easier!</strong>
</p>
<p align="center">
	<a href="https://solon.noear.org/">https://solon.noear.org</a>
</p>

<p align="center">
    <a target="_blank" href="https://central.sonatype.com/search?q=org.noear%3Asolon-parent">
        <img src="https://img.shields.io/maven-central/v/org.noear/solon.svg?label=Maven%20Central" alt="Maven" />
    </a>
    <a target="_blank" href="LICENSE">
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
    <a target="_blank" href="https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html">
		<img src="https://img.shields.io/badge/JDK-21-green.svg" alt="jdk-21" />
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

##### Language: English | [中文](README_CN.md) | [Русский](README_RU.md) | [日本語](README_JP.md)

<hr />

Startup is 5 to 10 times faster; QPS is 2 to 3 times higher; runtime memory is saved by 1/3 to 1/2; packaging can be reduced to 1/2 to 1/10

<hr />

## Introduction:

Build from scratch. It has its own standards and norms and open ecology. Combination of different ecological plug-ins to meet different needs, convenient customization, rapid development:

* **Restraint, simplicity, efficiency, openness and ecology**
* Supports JDK8, JDK11, JDK17, JDK21
* Http, WebSocket, Socket three signal unified development experience (commonly known as: three-source integration)
* Supports "annotation" and "manual" modes, freely controlled as needed
* Not Servlet, can adapt to any basic communication framework (minimum 0.3m running rpc architecture)
* [Unique IOC/AOP container design](https://solon.noear.org/article/241). It won't start slowly just because there are more plug-ins
* Supports Web, Data, Job, Remoting, Cloud, and other development scenarios
* The two architecture modes are Handler + Context and Listener + Message
* Emphasizes plug-in extensibility, expandable and swappable, adaptable to different application scenarios
* Supports GraalVm Native Image packaging. Support FaaS pattern development
* Allow service plug-ins to hot-plug, hot-swap, and hot-manage.

## Ecosystem Architecture Diagram:

* solon

<img src="solon_schema.png" width="700" />

* solon cloud

<img src="solon_cloud_schema.png" width="700" />

## Official website and related examples, cases：

* Official website address：[https://solon.noear.org](https://solon.noear.org)
* Official website supporting demos：[https://gitee.com/noear/solon-examples](https://gitee.com/noear/solon-examples)
* Project unit test：[__test](./__test/)
* Project more feature examples：[solon_api_demo](https://gitee.com/noear/solon_api_demo)  、 [solon_rpc_demo](https://gitee.com/noear/solon_rpc_demo) 、 [solon_socketd_demo](https://gitee.com/noear/solon_socketd_demo) 、 [solon_cloud_demo](https://gitee.com/noear/solon_cloud_demo) 、 [solon_auth_demo](https://gitee.com/noear/solon_auth_demo)
* User case：[User open source project](https://solon.noear.org/article/555)、[User business project](https://solon.noear.org/article/cases)

## Special thanks to JetBrains for supporting open-source projects：

<a href="https://jb.gg/OpenSourceSupport">
  <img src="https://user-images.githubusercontent.com/8643542/160519107-199319dc-e1cf-4079-94b7-01b6b8d23aa6.png" align="left" height="100" width="100"  alt="JetBrains">
</a>

