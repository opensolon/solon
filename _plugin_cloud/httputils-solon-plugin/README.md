
预热示例：

```java
public class App {
    public static void maing(String[] args) {
        Solon.start(App.class, args);
        
        //用http预热
        PreheatUtils.preheat("/run/check/");

        //用bean预热
        HelloService service = Aop.get(HelloService.class);
        service.hello();
    }
}
```