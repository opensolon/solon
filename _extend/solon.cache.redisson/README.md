
#### 配置

```yaml
#完整配置示例
solon.cache1:
  driverType: "redis"
  keyHeader: "demo" #默认为 ${solon.app.name} ，可不配置
  defSeconds: 30 #默认为 30，可不配置
  server: "localhost:6379"
  db: 0 #默认为 0，可不配置
  password: ""
  maxTotal: 200 #默认为 200，可不配


#简配示例
solon.cache2:
  server: "localhost:6379"
```

#### 代码

```java
//构建 bean
@Configuration
public class Config {
    @Bean(value = "cache1", typed = true) //默认
    public CacheService cache1(@Inject("${solon.cache1}") RedissonCacheService cache){
        return cache;
    }

    @Bean("cache2")
    public CacheService cache2(@Inject("${solon.cache2}") CacheServiceSupplier supplier){
        //CacheServiceSupplier 可自动识别类型
        return supplier.get();
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