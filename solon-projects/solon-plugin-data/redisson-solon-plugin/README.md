
配置示例::

```yaml
redisson.demo1:
  file: "classpath:redisson.yml"

redisson.demo2:
  config: |
    singleServerConfig:
      idleConnectionTimeout: 10000
      connectTimeout: 10000
      timeout: 3000
      retryAttempts: 3
      retryInterval: 1500
      password: null
      subscriptionsPerConnection: 5
      clientName: null
      address: "redis://192.168.88.60:6379"
      subscriptionConnectionMinimumIdleSize: 1
      subscriptionConnectionPoolSize: 50
      connectionMinimumIdleSize: 24
      connectionPoolSize: 64
      database: 0
      dnsMonitoringInterval: 5000
    threads: 16
    nettyThreads: 32
    codec: !<org.redisson.codec.Kryo5Codec> { }
    transportMode: "NIO"
```


```yaml
# 模式一:: 在resources目录下，添加 redisson.yml 文件，内容如下：
# Demo配置
singleServerConfig:
  idleConnectionTimeout: 10000
  connectTimeout: 10000
  timeout: 3000
  retryAttempts: 3
  retryInterval: 1500
  password: null
  subscriptionsPerConnection: 5
  clientName: null
  address: "redis://192.168.88.60:6379"
  subscriptionConnectionMinimumIdleSize: 1
  subscriptionConnectionPoolSize: 50
  connectionMinimumIdleSize: 24
  connectionPoolSize: 64
  database: 0
  dnsMonitoringInterval: 5000
threads: 16
nettyThreads: 32
codec: !<org.redisson.codec.Kryo5Codec> {}
transportMode: "NIO"
```
注入示例：

```java
@Configuration
public class Config {
    
    @Bean(value = "demo1",typed = true)
    public RedissonClient demo1(@Inject("${redisson.demo1}") RedissonSupplier supplier) {
        return supplier.get();
    }

    @Bean(value = "demo2")
    public RedissonClient demo2(@Inject("${redisson.demo2}") RedissonSupplier supplier) {
        return supplier.get();
    }
}

@Component
public class DemoService {
    
    @Inject("demo1")
    RedissonClient demo1;

    @Inject("demo2")
    RedissonClient demo2;
    
}
```