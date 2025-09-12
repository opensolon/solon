<h1 align="center" style="text-align:center;">
<img src="solon_icon.png" width="128" />
<br />
Solon v3.6.0-SNAPSHOT
</h1>
<p align="center">
	<strong>面向全场景的 Java 企业级应用开发框架</strong>
    <br/>
    <strong>克制、高效、开放</strong>
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
    <a target="_blank" href="https://www.oracle.com/java/technologies/javase/jdk24-archive-downloads.html">
		<img src="https://img.shields.io/badge/JDK-24-green.svg" alt="jdk-24" />
	</a>
    <br />
    <a target="_blank" href='https://gitee.com/opensolon/solon/stargazers'>
		<img src='https://gitee.com/opensolon/solon/badge/star.svg?theme=gvp' alt='gitee star'/>
	</a>
    <a target="_blank" href='https://github.com/opensolon/solon/stargazers'>
		<img src="https://img.shields.io/github/stars/opensolon/solon.svg?style=flat&logo=github" alt="github star"/>
	</a>
    <a target="_blank" href='https://gitcode.com/opensolon/solon/stargazers'>
		<img src='https://gitcode.com/opensolon/solon/star/badge.svg' alt='gitcode star'/>
	</a>
</p>


##### 语言： 中文 | [English](README_EN.md) | [Русский](README_RU.md) | [日本語](README_JP.md)

<hr />

<p align="center">
并发高 700%；内存省 50%；启动快 10 倍；打包小 90%；同时支持 java8 ~ java24, native 运行时。
<br/>
从零开始构建，有更灵活的接口规范与开放生态
</p>

<hr />

## 特性:

| 特性         | 描述                                                                                                             | 
|------------|----------------------------------------------------------------------------------------------------------------| 
| 更高的计算性价比   | 并发高 700%（[techempower](https://www.techempower.com/benchmarks/#hw=ph&test=plaintext&section=data-r23)）；内存省 50% |
| 更快的开发效率    | 代码少；入门简单；启动快 10倍（调试快）                                                                                          |
| 更好的生产与部署体验 | 打包小 90%                                                                                                        |
| 更大的兼容范围    | 非 java-ee 架构；同时支持 java8 ～ java24，graalvm native image                                                          |


## 主要代码仓库


| 代码仓库                                                             | 描述                               | 
|------------------------------------------------------------------|----------------------------------| 
| [/opensolon/solon](../../../../opensolon/solon)                             | Solon ,主代码仓库                     | 
| [/opensolon/solon-examples](../../../../opensolon/solon-examples)           | Solon ,官网配套示例代码仓库                |
|                                                                  |                                  |
| [/opensolon/solon-expression](../../../../opensolon/solon-expression)                   | Solon Expression ,代码仓库           | 
| [/opensolon/solon-flow](../../../../opensolon/solon-flow)                   | Solon Flow ,代码仓库                 | 
| [/opensolon/solon-ai](../../../../opensolon/solon-ai)                       | Solon Ai ,代码仓库                   | 
| [/opensolon/solon-cloud](../../../../opensolon/solon-cloud)                 | Solon Cloud ,代码仓库                | 
| [/opensolon/solon-admin](../../../../opensolon/solon-admin)                 | Solon Admin ,代码仓库                | 
| [/opensolon/solon-jakarta](../../../../opensolon/solon-jakarta)             | Solon Jakarta ,代码仓库（base java21） | 
| [/opensolon/solon-integration](../../../../opensolon/solon-integration)     | Solon Integration ,代码仓库          | 
|                                                                  |                                  |
| [/opensolon/solon-gradle-plugin](../../../../opensolon/solon-gradle-plugin) | Solon Gradle ,插件代码仓库             | 
| [/opensolon/solon-idea-plugin](../../../../opensolon/solon-idea-plugin)     | Solon Idea ,插件代码仓库               | 
| [/opensolon/solon-vscode-plugin](../../../../opensolon/solon-vscode-plugin) | Solon VsCode ,插件代码仓库             | 


## 生态架构图：

* solon

<img src="solon_schema.png" width="700" />

* solon cloud

<img src="solon_cloud_schema.png" width="700" />


## 官网及相关示例、案例：

* 官网地址：[https://solon.noear.org](https://solon.noear.org)
* 官网配套演示：[https://gitee.com/opensolon/solon-examples](https://gitee.com/opensolon/solon-examples)
* 项目单测：[__test](./__test/) 
* 用户案例：[用户开源项目](https://solon.noear.org/article/555)、[用户商业项目](https://solon.noear.org/article/cases)

## 特别感谢JetBrains对开源项目支持：

<a href="https://jb.gg/OpenSourceSupport">
  <img src="https://user-images.githubusercontent.com/8643542/160519107-199319dc-e1cf-4079-94b7-01b6b8d23aa6.png" align="left" height="100" width="100"  alt="JetBrains">
</a>

