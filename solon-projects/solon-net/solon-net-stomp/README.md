有些中间件参考：

https://blog.csdn.net/weixin_43871956/article/details/91350748

### 使用说明：

* 注册经理人

```java
@ServerEndpoint("/chat")
public class ChatStompBroker extends StompBroker {
    
}
```

* 业务场景应用

```java
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Http;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.To;

@Controller
public class TestController {
    @Inject //@Inject("/chat") 多经纪人时，指定名字
    StompSender stompSender;

    @Mapping("/hello")
    @To("/topic/greetings")
    public Greeting greeting(HelloMessage message) throws Exception {
        Thread.sleep(1000); // simulated delay
        return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
    }

    @Mapping("/topic/greetings")
    public Greeting greeting2(HelloMessage message) throws Exception {
        Thread.sleep(1000); // simulated delay
        //log.info ("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
        return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
    }

    @Http
    @Mapping("/hello2")
    public void greeting3(Context ctx, HelloMessage message) throws Exception {
        Thread.sleep(1000); // simulated delay

        String payload = ctx.renderAndReturn(new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!"));
        stompSender.sendTo("/topic/greetings", payload);
    }

    @Mapping("/sendToUser")
    public void sendToUser(Context ctx, HelloMessage message) throws Exception {
        Thread.sleep(1000); // simulated delay

        String payload = ctx.renderAndReturn(new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!"));
        stompSender.sendTo("/topic/user/" + message.getUserId() + "/sendToUser", payload);
    }
}
```
