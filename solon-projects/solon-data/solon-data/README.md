
基于配置构建数据源

```yaml
solon.dataSources:
  db_order!: #数据源（!结尾表示 typed=true）
    class: "com.zaxxer.hikari.HikariDataSource"
    driverClassName: "xx"
    url: "xxx"
    username: "xxx"
    paasword: "xxx"
    untransaction: "true"
  db_user: #动态数据源
    class: "org.noear.solon.data.dynamicds.DynamicDataSource"
    strict: true #是否严格的
    default: db_user_1 #默认子数据源
    db_user_1: #内部数据源1
      dataSourceClassName: "com.zaxxer.hikari.HikariDataSource"
      driverClassName: "xx"
      jdbcUrl: "xxx" #属性名要与 type 类的属性对上
      username: "xxx"
      paasword: "xxx"
    db_user_2: #内部数据源2
      dataSourceClassName: "com.zaxxer.hikari.HikariDataSource"
      driverClassName: "xx"
      jdbcUrl: "xxx" #属性名要与 type 类的属性对上
      username: "xxx"
      paasword: "xxx"
  db_log: #分片数据源
    class: "org.noear.solon.data.shardingds.ShardingDataSource"
    file: "classpath:sharding.yml"
```
