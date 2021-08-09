
**配置**

```yaml
solon.mime.json: "application/json"

solon.staticfiles:
  enabled: true
  maxAge: 6000
```

**代码**

```java
public class App {
    public static void main(String[] args) {
        Solon.start(App.class, args);

        //添加静态目录印射
        StaticMappings.add("/", new ExtendStaticRepository());
        StaticMappings.add("/", new FileStaticRepository("/data/sss/eater/water_ext/"));
        StaticMappings.add("/", new ClassPathStaticRepository("/user/"));
    }
}
```