

服务调用示例：

```java
public class App {
    public static void maing(String[] args) {
        Solon.start(App.class, args);

        //通过服务名进行http请求
        HttpUtils.http("HelloService","/hello").get();
    }
}
```




预热示例：

```java
public class App {
    public static void maing(String[] args) {
        Solon.start(App.class, args);

        //用http请求自己进行预热
        PreheatUtils.preheat("/healthz");

        //用bean预热
        HelloService service = Aop.get(HelloService.class);
        service.hello();
    }
}
```

