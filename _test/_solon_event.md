
* org.eclipse.jetty.server.Server
* io.undertow.Undertow.Builder
* BeanWrap
* Throwable

```java
@Configuration
public class ServerCustom implements EventListener<Undertow.Builder>{
    public void onEvent(Undertow.Builder sc){
        sc.setServerOption(UndertowOptions.ALWAYS_SET_KEEP_ALIVE, false);
    }
}
```