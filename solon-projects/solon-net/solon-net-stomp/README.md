有些中间件参考：

https://blog.csdn.net/weixin_43871956/article/details/91350748


### 接口说明

| 注解表达式                      | 对应发射器接口                                           |         |
|----------------------------|---------------------------------------------------|---------|
| `@To("*:destination")`     | `StompEmitter.sendTo(destination,..)`             | 发给所有订阅者 |
| `@To(".:destination")`     | `StompEmitter.sendToSession(destination,..)`      | 发给当前用户  |
| `@To("user1:destination")` | `StompEmitter.sendToUser("user1",destination,..)` | 发给特定用户  |



### 使用说明：

* 注册经理人

```java
@ServerEndpoint("/chat")
public class ChatStompBroker extends StompBroker {
    public ChatStompBroker(){
        //转发到 Solon Handler 体系（即 @Mapping 函数）
        this.addServerListener(new ToHandlerStompListener(this));
    }
}
```

* 业务场景应用

```java
@Controller
public class TestController {
    @Inject //@Inject("/chat") 多经纪人时，指定名字
    StompEmitter stompEmitter;

    @Message
    @Mapping("/app/hello")
    @To("*:/topic/greetings") //发给所有订阅者
    public Greeting greeting(HelloMessage message) throws Exception {
        Thread.sleep(1000); // simulated delay
        return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
    }
    
    @Message
    @Mapping("/topic/hello")
    @To(".:/topic/greetings") //发给当前用户
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
        stompEmitter.sendTo("/topic/greetings", payload);
    }
}
```
