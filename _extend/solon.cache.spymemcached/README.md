

#### 配置

```yaml
#完整配置示例
solon.cache1:
  keyHeader: "demo" #默认为 ${solon.app.name} ，可不配置
  defSeconds: 30 #默认为 30，可不配置
  server: "localhost:6379"
  user: "" #默认为空，可不配置
  password: "" #默认为空，可不配置


#简配示例
solon.cache2:
  server: "localhost:6379"
```

#### 代码

```java
//构建 bean
@Configuration
public class Config {
    @Bean
    public CacheService cache1(@Inject("${solon.cache1}") MemCacheService cache){
        return cache;
    }

    @Bean
    public CacheService cache2(@Inject("${solon.cache2}") MemCacheService cache){
        return cache;
    }
}

//应用
@Controller
public class DemoController {
    @Cache
    public String hello(String name) {
        return String.format("Hello {0}!", name);
    }
}
```