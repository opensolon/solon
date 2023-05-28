


```yaml
redisson.demo1: 
  file: "classpath:redisson.yaml"
```


```yaml
redisson.demo2: 
  config: |
    clusterServersConfig:
      idleConnectionTimeout: 10000
      connectTimeout: 10000
      timeout: 3000
      retryAttempts: 3
      retryInterval: 1500
      failedSlaveReconnectionInterval: 3000
      failedSlaveCheckInterval: 60000
      password: null
      subscriptionsPerConnection: 5
      clientName: null
      loadBalancer: !<org.redisson.connection.balancer.RoundRobinLoadBalancer> {}
      subscriptionConnectionMinimumIdleSize: 1
      subscriptionConnectionPoolSize: 50
      slaveConnectionMinimumIdleSize: 24
      slaveConnectionPoolSize: 64
      masterConnectionMinimumIdleSize: 24
      masterConnectionPoolSize: 64
      readMode: "SLAVE"
      subscriptionMode: "SLAVE"
      nodeAddresses:
      - "redis://127.0.0.1:7004"
      - "redis://127.0.0.1:7001"
      - "redis://127.0.0.1:7000"
      scanInterval: 1000
      pingConnectionInterval: 0
      keepAlive: false
      tcpNoDelay: false
    threads: 16
    nettyThreads: 32
    codec: !<org.redisson.codec.Kryo5Codec> {}
    transportMode: "NIO"
```