
* org.eclipse.jetty.server.Server
* io.undertow.Undertow.Builder
* BeanWrap
* Throwable

```java
@XConfiguration
public class ServerCustom implements XEventListener<Undertow.Builder>{
    public void onEvent(Undertow.Builder sc){
        sc.setServerOption(UndertowOptions.ALWAYS_SET_KEEP_ALIVE, false);
    }
}
```