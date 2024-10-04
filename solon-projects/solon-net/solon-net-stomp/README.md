

### 使用说明：

* 注册适配器

```java
@ServerEndpoint("/chat")
public class ChatToStompWebSocketAdapter extends ToStompWebSocketAdapter {
    
}
```

* 使用发送器

```java
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;

@Controller
public class DemoController() {
    @Inject
    StompMessageSender messageSender;

    @Inject("/chat") //有多个地址监听时，需要与 @ServerEndpoint("/chat") 对应起来
    StompMessageSender messageSender1;

    @Mapping("test")
    public void test() {
        messageSender.sendTo("/topic/todoTask1/1", "test");
    }
}
```
