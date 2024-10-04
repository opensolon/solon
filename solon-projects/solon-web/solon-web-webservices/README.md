```xml
<dependency>
    <groupId>org.noear</groupId>
    <artifactId>solon-web-webservices</artifactId>
</dependency>
```


### 1、服务端示例

服务端需要与 solon.boot.jetty、solon.boot.unertow 或者 war 部署方式，配合使用。

* 添加配置（指定 web service 路径段）

```yaml
server.webservices.path: "/ws/" #默认为 ws
```

* 添加服务代码

```java
public class ServerTest {
    public static void main(String[] args) {
        Solon.start(ServerTest.class, args);
    }

    @WebService(serviceName = "HelloService", targetNamespace = "http://demo.solon.io")
    public static class HelloServiceImpl {
        public String hello(String name) {
            return "hello " + name;
        }
    }
}
```

启动后，可以通过 `http://localhost:8080/ws/HelloService?wsdl` 查看 wsdl 信息。

### 2、客户端示例

* 手写模式

```java
public class ClientTest {
    public static void main(String[] args) {
        String wsAddress = "http://localhost:8080/ws/HelloService";
        HelloService helloService = WebServiceHelper.createWebClient(wsAddress, HelloService.class);

        System.out.println("rst::" + helloService.hello("noear"));
    }

    @WebService(serviceName = "HelloService", targetNamespace = "http://demo.solon.io")
    public interface HelloService {
        @WebMethod
        String hello(String name);
    }
}
```

* 容器模式

使用 `@WebServiceReference` 注解，直接注入服务。

```java
//定义测试控制器
@Controller
public static class DemoController {
    @WebServiceReference("http://localhost:8080/ws/HelloService")
    private HelloService helloService;

    @Mapping("/test")
    public String test() {
        return helloService.hello("noear");
    }
}

//配置 WebService 接口
@WebService(serviceName = "HelloService", targetNamespace = "http://demo.solon.io")
public interface HelloService {
    @WebMethod
    String hello(String name);
}

//启动 Solon
public class ClientTest {
    public static void main(String[] args) {
        Solon.start(ClientTest2.class, args);
    }
}
```
