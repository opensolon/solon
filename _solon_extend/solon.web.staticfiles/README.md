
**配置**

```yaml
solon.mime.json: "application/json"

solon.staticfiles:
  enabled: true
  cacheMaxAge: 6000
  mappings:
    - path: "/img/"
      repository: "/data/sss/app/" #表示磁盘目录地址
    - path: "/"
      repository: "classpath:user" #表示资源目录
    - path: "/"
      repository: ":extend" #表示扩展静态静态

solon.extend: "!jt_ext" #!开头，表示如果没有扩展目录则自动创建 //用于支持 ExtendStaticRepository
```

**代码**

```java
public class App {
    public static void main(String[] args) {
        Solon.start(App.class, args, app -> {
            //添加静态目录印射

            //1.添加扩展目录：${solon.extend}/static/
            StaticMappings.add("/", new ExtendStaticRepository());
            //2.添加本地绝对目录
            StaticMappings.add("/", new FileStaticRepository("/data/sss/water/water_ext/"));
            //3.添加资源路径
            StaticMappings.add("/", new ClassPathStaticRepository("user"));
        });
    }
}
```