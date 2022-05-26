

```yml
server.session:
  timeout: 7200  #超时配置。单位秒（可不配，默认：7200）
  state.domain: "solon.noear.org" #可共享域配置（可不配，默认当前服务域名；多系统共享时要配置）
  state.redis:
    server: "redis.io:6379" #redis 连接地址
    password: 1234 #redis 连接密码
    db: 31 #如果是集群模式，db 无效
    idleConnectionTimeout: 10000
    connectTimeout: 10000
  
# http://www.voidcc.com/redisson/redisson-common-configuration
```