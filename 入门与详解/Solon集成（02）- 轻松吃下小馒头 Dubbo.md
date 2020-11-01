Dubbo是阿里巴巴公司开源的一个高性能优秀的服务框架，使得应用可通过高性能的 RPC 实现服务的输出和输入功能，可以和Spring框架无缝集成。现在，也可以和Solon框架无缝集成。。。今天主要讲讲Solon如何集成官方的Dubbo。


### 一、运行工具与环境
* 运行环境：JDK 8，Maven 4.0
* 技术栈：Solon 1.1+、Dubbo 2.7.5+、Nacos 1.3+
* 工具：IntelliJ IDEA、谷歌浏览器

### 二、Springboot快速集成Dubbo关键的依赖
```xml
<dependency>
    <groupId>org.noear</groupId>
    <artifactId>dubbo-solon-plugin</artifactId>
    <version>1.1.7</version>
</dependency>
```

### 三、如何使用
* 1.使用Dubbo要知道服务提供者和消费者概念，而且调用的服务接口最好是共同的Api，如下图是我写的入门项目。

![](/img/6199f6ddec0e44fa8099d5b42ce8aa88.png)

* 2.配置application.yml

```yml
server.port: 8011

dubbo:
  application:
    name: hello
    owner: noear
  registry:
    address: nacos://192.168.8.118:8848
```

这边用到的注册中心是阿里的Nacos，当然你也可以用其他注册中心，你可以查看Dubbo文档，里面有其他注册中心使用方法。其他一些配置可以去官网查看，这边就不在多说。

* 3.服务提供者和消费者

**共同的接口：**

```java
public interface HelloService {
    String sayHello(String name);
}
```

这就是服务提供者和消费者共同调用的接口了。简单了点儿。

**服务提供者：**

```java
@Service(group = "hello")
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String name) {
        return "hello, " + name;
    }
}

@EnableDubbo
public class DubboProviderApp {
    //服务端启动入口
    public static void main(String[] args) {
        XApp.start(DubboProviderApp.class, args);
    }
}
```

简单来说就是将上面接口的实现方法（称之为服务）注册到注册中心，并暴露端口供其他消费者消费。@Service里面的一些信息就是服务具体的注册地址。

**服务消费者：**

```java
@EnableDubbo
@XController
public class DubboConsumeApp {
    //同时加个应用启动入口；方便测试
    public static void main(String[] args) {
        XApp.start(DubboConsumeApp.class, args, app -> app.enableHttp(false));

        //通过手动模式直接拉取bean
        DubboConsumeApp tmp = Aop.get(DubboConsumeApp.class);
        System.out.println(tmp.home());
    }

    @Reference(group = "hello")
    HelloService helloService;

    @XMapping("/")
    public String home() {
        return helloService.sayHello("noear");
    }
}
```

如上@Reference的直接表示远程调用，里面最重要的是url，表示要调用的地址，也就是上面服务暴露的地址。


### 四、运行项目
首先启动服务提供者，在启动服务调用者，打开谷歌浏览器，输入 `http://localhost:8080/`，输出：
```
hello, noear
```

#### 五、项目地址

[https://gitee.com/noear/solon_demo/tree/master/demo23.solon_dubbo_sml](https://gitee.com/noear/solon_demo/tree/master/demo23.solon_dubbo_sml)