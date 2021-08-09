```yaml
solon.mime.json: "application/json"

solon.staticfiles:
  enabled: true
  maxAge: 6000
```

**添加静态目录**

```java
import org.noear.solon.Solon;
import org.noear.solon.extend.staticfiles.StaticMappings;
import org.noear.solon.extend.staticfiles.repository.ClassPathStaticRepository;
import org.noear.solon.extend.staticfiles.repository.ExtendStaticRepository;
import org.noear.solon.extend.staticfiles.repository.FileStaticRepository;

public class App {
    public static void main(String[] args) {
        Solon.start(App.class, args);

        StaticMappings.add("/", new ExtendStaticRepository());
        StaticMappings.add("/", new FileStaticRepository("/data/sss/eater/water_ext/"));
        StaticMappings.add("/", new ClassPathStaticRepository("/user/"));
    }
}
```