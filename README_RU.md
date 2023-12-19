<h1 align="center" style="text-align:center;">
<img src="solon_icon.png" width="128" />
<br />
Solon v2.6.4-SNAPSHOT
</h1>
<p align="center">
	<strong>Новая структура разработки приложений Java, меньше, быстрее и проще!</strong>
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

##### язык： Русский | [中文](README_CN.md)  | [English](README_EN.md) | [日本語](README_JP.md)

<hr />

Запуск в 5-10 раз быстрее; QPS в 2 — 3 раза выше; Экономия оперативной памяти на 1/3-1/2; Упаковка может быть сокращена до 1/2-1/10

<hr />

## 介绍：

Постройка с нуля. Существуют собственные стандартные нормы и открытая экология. Комбинировать различные эко-плагины для решения различных потребностей, удобно настраивать их и быстро разрабатывать:

* **Сдержанность, лаконичность, эффективность, открытость, экология**
* Поддержка JDK8, JDK11, JDK17, JDK21
* Http, WebSocket, Socket, три общих опыта разработки сигналов.
* Поддерживайте "интубацию" и "ручное управление", свободно управляемое по требованию
* Не Servlet, который может быть адаптирован к любой базовой структуре связи (минимальная эксплуатация RPC архитектуры на 0,3 м)
* [Уникальный дизайн контейнера IOC/AOP](https://solon.noear.org/article/241). Он не заводится медленнее, потому что плагин становится больше
* Поддерживаем любые сценарии развития, такие как Web, Data, Job, Remoting, Cloud и т.д
* Совместить архитектурные модели Handler + Context и Listener + Message
* Подчеркивает расширение плагина, расширяемое и переключаемое; Адаптация к различным параметрам применения
* В поддержку "GraalVm Native Image"
* Позволяет бизнес-плагины "включать", "вырывать", "управление теплом"


## Экологическая архитектура：

* solon

<img src="solon_schema.png" width="700" />

* solon cloud

<img src="solon_cloud_schema.png" width="700" />

## Официальная сеть и связанные с ней примеры, дела：

* Адрес основной сети：[https://solon.noear.org](https://solon.noear.org)
* Демо в комплекте с официальной сетью：[https://gitee.com/noear/solon-examples](https://gitee.com/noear/solon-examples)
* Монометрия проекта：[__test](./__test/) 
* Более функциональный пример проекта：[solon_api_demo](https://gitee.com/noear/solon_api_demo)  、 [solon_rpc_demo](https://gitee.com/noear/solon_rpc_demo) 、 [solon_socketd_demo](https://gitee.com/noear/solon_socketd_demo) 、 [solon_cloud_demo](https://gitee.com/noear/solon_cloud_demo) 、 [solon_auth_demo](https://gitee.com/noear/solon_auth_demo)
* Дело пользователя：[Пользовательский проект с открытым исходным кодом](https://solon.noear.org/article/555)、[Коммерческий проект пользователя](https://solon.noear.org/article/cases)


## Особая благодарность JetBrains за поддержку проекта open source：

<a href="https://jb.gg/OpenSourceSupport">
  <img src="https://user-images.githubusercontent.com/8643542/160519107-199319dc-e1cf-4079-94b7-01b6b8d23aa6.png" align="left" height="100" width="100"  alt="JetBrains">
</a>

