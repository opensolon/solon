### Solon的核心

在上篇中我们成功运行了一个简单的web应用；本篇将对它的启动过程、扩展体系和应用属性配置进行介绍。

#### （一）XApp.start(source, args, builder) 内部执行过程（即Solon的启动过程）

1. 实例化 XApp.global() 
2. 加载应用属性配置
3. 加载扩展文件夹
4. 扫描插件并排序记录（插件也可叫扩展组件）
5. 运行builder函数（如果它不为null）
6. 运行插件
7. 扫描source目录并加载java bean
8. 加载渲染关系
9. 完成

> 了解这个过程非常之重要，尤其是有兴致开发插件的同学：你的插件在运行之前，配置已经存在了，但java bean仍未加载。

#### （二）XPlugin 插件体系

Solon 的插件也可以叫扩展组件，相当于Spring 的 starter。Solon已经提供了大量的基础插件，但对第三方的框架适配目前较少。

| 插件 | 说明 |
| --- | --- |
| boot插件:: | 说明 |
| org.noear:solon.boot.jlhttp* | boot插件,对`jlhttp`适配,提供`http`服务（不自带session state） |
| org.noear:solon.boot.jetty* | boot插件,对`jetty`适配,提供`http`服务（网友@khb提供） |
| org.noear:solon.boot.undertow* | boot插件,对`undertow`适配,提供`http`服务（网友@tyk提供） |
| org.noear:solon.boot.websocket | boot插件,对`java-websocket`适配，提供`websocket`服务 |
| org.noear:solon.boot.smartsocket | boot插件,对`smart-bsocket`适配，提供`socket`服务 |
| org.noear:solon.extend.jetty.jsp | 扩展插件,为`jetty`添加`jsp`支持（不建议使用jsp）（网友@khb提供） |
| org.noear:solon.extend.undertow.jsp | 扩展插件,为`undertow`添加`jsp`支持（不建议使用jsp）（网友@tyk提供） |
| | |
| 静态文件支持插件:: | 说明 |
| org.noear:solon.extend.staticfiles | 扩展插件,添加静态文件支持（监视 resources/static 文件夹） |
| | |
| 切面支持插件:: | 说明 |
| org.noear:solon.extend.aspect | 扩展插件,添加XDao、XService注解支持；进而支持事务和缓存注解 |
| | |
| 数据操作支持插件:: | 说明 |
| org.noear:solon.extend.data | 扩展插件,实现事务和缓存的注解支持 |
| | |
| 验证操作支持插件:: | 说明 |
| org.noear:solon.extend.validation | 扩展插件,实现验证类注解支持 |
| | |
| Yaml配置支持插件:: | 说明 |
| org.noear:solon.extend.properties.yaml | 扩展插件,添加yml配置文件支持 |
| | |
| 定时任务支持插件:: | 说明 |
| org.noear:solon.extend.schedule | 扩展插件,实现定时任务支持（和 cron4j-solon-plugin 风格不同） |
| | |
| 远程关闭支持插件:: | 说明 |
| org.noear:solon.extend.stop | 扩展插件,实现远程关闭支持 |
| | |
| Session插件:: | 说明（可将boot插件的session state服务，自动换掉） |
| org.noear:solon.extend.sessionstate.local | 扩展插件,本地`session` |
| org.noear:solon.extend.sessionstate.redis | 扩展插件,分布式`session`（其于`redis`构建） |
| | |
| 序列化插件:: | 说明 |
| org.noear:solon.serialization.fastjson* | 序列化插件，对 `fastjson` 适配，提供`json`视图输出 或 序列化输出 |
| org.noear:solon.serialization.snack3* | 序列化插件，对 `snack3` 适配，提供`json`视图输出 或 序列化输出 |
| org.noear:solon.serialization.hession* | 序列化插件，对 `hession` 适配，提供 `hession` 序列化输出 |
| org.noear:solon.serialization.jackson | 序列化插件，对 `jackson` 适配，提供`json`视图输出 或 序列化输出 |
| | |
| 视图插件:: | 说明（可置多个视图插件） |
| org.noear:solon.view.freemarker* | 视图插件，对 `freemarker` 适配，提供`html`视图输出 |
| org.noear:solon.view.jsp | 视图插件，对 `jsp` 适配，提供`html`视图输出 |
| org.noear:solon.view.velocity | 视图插件，对 `velocity` 适配，提供`html`视图输出 |
| org.noear:solon.view.thymeleaf | 视图插件，对 `thymeleaf` 适配，提供`html`视图输出 |
| org.noear:solon.view.beetl | 视图插件，对 `beetl` 适配，提供`html`视图输出 |
| org.noear:solon.view.enjoy | 视图插件，对 `enjoy` 适配，提供`html`视图输出 |
| | |
| rpc client:: | 说明 |
| org.noear:solonclient | solon rpc client 与solon 的 rpc service 配对 |
| | |
| 外部框架适配:: | 说明 |
| org.noear:beetlsql-solon-plugin | beetlsql 适配插件 |
| org.noear:cron4j-solon-plugin | cron4j 适配插件 |
| org.noear:dubbo-solon-plugin | dubbo 适配插件|
| org.noear:feign-solon-plugin | feign 适配插件|
| org.noear:mybatis-solon-plugin | mybatis 适配插件|
| org.noear:mybatis-sqlhelper-solon-plugin | mybatis 分页适配插件|
| org.noear:weed3-solon-plugin | weed3 适配插件|

怎么使用？直接在pom.xml中添加依赖即可。

#### （三）应用属性配置文件

Solon项目会使用一个全局的属性配置文件 application.properties 或者是 application.yml，在resources目录下。

Solon会根据在pom.xml中依赖的jar包进行自动配置，当我们要对这些jar包对应的框架进行配置又该怎么办呢？没错，可以在全局配置文件（application.properties 或者是 application.yml）中进行配置，如http server的端口配置等。

##### a.基础约定（不能改，为了简化套路）

```xml
//资源路径说明（不用配置；也不能配置）
resources/application.properties（或 application.yml） 为应用配置文件
resources/static/ 为静态文件根目标
resources/WEB-INF/view/ 为视图模板文件根目标（支持多视图共存）

//调试模式：
启动参数添加：-debug=1 或 --debug=1
```

##### b.端口配置（以使用 application.yml 为例）
```yml
server.port: 8080
```
##### c.请求包大小限制
```yml
server.request.maxRequestSize: 2Mb
```
##### d.会话超时
```yml
server.session.timeout: 3600 #单位:s
```

##### e.视图引擎配置（可多引擎共享）

原则上不要加这些配置，更不要修改；想用哪个模板，加哪个组件即可（有利于简化套路）

```yml
#默认的配置（不需要改配置，除非要修改）
solon.view.mapping.htm: BeetlRender #简写
solon.view.mapping.shtm: EnjoyRender
solon.view.mapping.ftl: FreemarkerRender #默认的模板引擎
solon.view.mapping.jsp: JspRender
solon.view.mapping.html: ThymeleafRender
solon.view.mapping.vm: org.noear.solon.view.velocity.VelocityRender #引擎全名（一般用简写）
```

##### f.分布式session配置

当添加org.noear:solon.extend.sessionstate.redis组件时，即切换为分布式session，需要以下配置了（它基于redis包装；也可以基于接口自己造一个）：

```yml
# 当使用 sesstionstate.redis 的配置
server.session.state.redis.server: 127.0.0.1:6379
server.session.state.redis.password: xxx
server.session.state.redis.db: 31
server.session.state.redis.maxTotaol: 200
```

##### g.统一的日志

Solon默认没有对接外部日志框架，而是通过事件总线接收应用内所有的异常。

```java
XApp.start(...).onEerror(err-> ..)
```

##### i.页面跳转

```java
ctx.redirect("http://www.noear.org");
//or
XContext.current.redirect("http://www.noear.org");
```

#### （四）其它配置说明

##### a.自定义属性

只要名字不冲突，随便加。例：
```yml
user.name: "lie lai"
```

##### b.属性引用（这个不支持；为了简化套路）
```yml
user.name: "lie lai"
message: "${user.name} 你好!" #这个不支持（有需要的时候，自己替换）
```

##### c.如何获取属性配置
```java
//注解模式
@XInject("${user.name}")

//代码模式
XApp.cfg().get("user.name")
```

##### d.属性转对象

这个功能用起来会很方便，简化不少的代码编写。

```java
//注解模式
//
@XConfiguration  // XConfiguration或别的类注解，都可
public class test{
    //注入字段，在任何托管Bean里有效
    //
    @XInject("${user}")
    UserModel user;
    
    //注入参数，只在@XConfiguration类有效
    //
    @XBean
    public Xxxxx buildXxxx(@XInject("${test.db1}") HikariDataSource dataSource){    
    
    }
}

//代码模式
UserModel user = XApp.cfg().getBean("user", UserModel.class);
HikariDataSource dataSource =  XApp.cfg().getBean("test.db1", HikariDataSource.class);
```


本篇到此结束，主要介绍了Solon中几个的问题：1，启动过程；2，扩展体系，3，应用属性配置，同时解决上篇中的几个问题，从下篇开始，将针对Solon的web开发进一步展开介绍。



