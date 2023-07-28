# lettuce-solon-plugin

> lettuce本身易于适配，方便使用，本插件只是做了一层薄薄的适配，方便与在某些情况下更加便利的使用。


## 依赖引入

```xml
<dependency>
    <groupId>org.noear</groupId>
    <artifactId>lettuce-solon-plugin</artifactId>
    <version>${solon.version}</version>
</dependency>
```

## 配置文件
    
```yaml
### 任意选一种
### 模式一
lettuce.rd1:
  # Redis模式 (standalone, cluster, sentinel)
  redis-mode: standalone
  redis-uri: redis://localhost:6379/0

#### 模式二
lettuce.rd2:
  # Redis模式 (standalone, cluster, sentinel)
  redis-mode: standalone
  config:
    host: localhost
    port: 6379
#    socket: xxxx
#    client-name: myClientName
#    database: 0
#    sentinel-masterId: 'mymaster'
#    username: 'myusername'
#    password: 'mypassword'
#    ssl: false
#    verify-mode: FULL
#    startTls: false
#    timeout: 10000
#    sentinels:
#      - host: localhost
#        port: 16379
#        password: 'mypassword'
#      - host: localhost
#        port: 26379
#        password: 'mypassword'
```
## java

### Config

```java
package io.lettuce.solon;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.RedisClusterClient;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

@Configuration
public class Config {

    @Bean(typed = true, name = "redisClient1")
    public RedisClient redisClient1(@Inject("${lettuce.rd1}") LettuceSupplier lettuceSupplier) {
        // 默认参数生成
        return (RedisClient) lettuceSupplier.get();
    }

    @Bean(name = "redisClient2")
    public RedisClient redisClient2(@Inject("${lettuce.rd2}") LettuceSupplier lettuceSupplier) {
        // 获取 配置文件解析的RedisURI
        RedisURI redisURI = lettuceSupplier.getRedisURI();
        // 手动创建
        RedisClient redisClient = RedisClient.create(redisURI);
        // 手动设置参数
        redisClient.setOptions(ClusterClientOptions.builder().validateClusterNodeMembership(false).build());
        return redisClient;
    }

    @Bean(name = "redisClusterClient1")
    public RedisClusterClient redisClusterClient1(@Inject("${lettuce.rd3}") LettuceSupplier lettuceSupplier) {
        // 默认参数生成
        return (RedisClusterClient) lettuceSupplier.get();
    }

    @Bean(name = "redisClusterClient2")
    public RedisClusterClient redisClusterClient2(@Inject("${lettuce.rd3}") LettuceSupplier lettuceSupplier) {
        // 获取 配置文件解析的RedisURI
        RedisURI redisURI = lettuceSupplier.getRedisURI();
        // 手动创建
        RedisClusterClient redisClusterClient = RedisClusterClient.create(redisURI);
        // 手动设置参数
        redisClusterClient.setOptions(ClusterClientOptions.builder().validateClusterNodeMembership(false).build());
        return redisClusterClient;
    }

}
```

### 使用

```java
@Component
public class DemoService {
    
    @Inject
    RedisClient redisClient;

    /**
     * 仅仅做测试使用，以实际情况为准
     */
    public void demoSet() {
        redisClient.connect().sync().set("test", "test");
        System.out.println(redisClient.connect().sync().get("test"));
        redisClient.connect().sync().del("test");
    }
}
```