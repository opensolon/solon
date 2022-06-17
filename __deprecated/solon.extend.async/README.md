
### 1.示例

```java
@EnableAsync
public class App {
    public static void main(String[] args) {
        Solon.start(App.class, args);
    }
}

@Service
public class AsyncTask {
    @Async
    public void snedMail(String mail, String title) {
        System.out.println("2");
    }
}

@Controller
public class DemoController {
    @Inject
    AsyncTask asyncTask;

    @Mapping("test")
    public void test(String mail, String title) {
        asyncTask.snedMail(mail, title);
        System.out.println("1");
    }
}
```

### 2.定制执行器

```java

```