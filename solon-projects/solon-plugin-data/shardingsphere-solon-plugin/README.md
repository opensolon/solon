
配置示例::

```yaml
# 模式一:: 支持：外置sharding.yml的配置
sharding.demo1:
  file: "classpath:sharding.yml"
  
# 模式二:: 支持：内置sharding.yml的配置
sharding.demo2:
  config: |
      mode:
        type: Standalone
        repository:
          type: JDBC
      dataSources:
        ds_1:
          dataSourceClassName: com.zaxxer.hikari.HikariDataSource
          driverClassName: com.mysql.jdbc.Driver
          jdbcUrl: jdbc:mysql://192.168.88.60:3306/xxxxxxx
          username: root
          password: xxxxxxx
        ds_2:
          dataSourceClassName: com.zaxxer.hikari.HikariDataSource
          driverClassName: com.mysql.jdbc.Driver
          jdbcUrl: jdbc:mysql://192.168.88.61:3306/xxxxxxx
          username: root
          password: xxxxxxx
      rules:
        - !READWRITE_SPLITTING
          dataSources:
            readwrite_ds:
              staticStrategy:
                writeDataSourceName: ds_1
                readDataSourceNames:
                  - ds_2
              loadBalancerName: random
          loadBalancers:
            random:
              type: RANDOM
      props:
        sql-show: true
```


```yaml
# 模式一:: 在resources目录下，添加 sharding.yml 文件，内容如下：
# Demo配置
mode:
  type: Standalone
  repository:
    type: JDBC
dataSources:
  ds_1:
    dataSourceClassName: com.zaxxer.hikari.HikariDataSource
    driverClassName: com.mysql.jdbc.Driver
    jdbcUrl: jdbc:mysql://192.168.88.60:3306/xxxx
    username: root
    password: xxxxxxx
  ds_2:
    dataSourceClassName: com.zaxxer.hikari.HikariDataSource
    driverClassName: com.mysql.jdbc.Driver
    jdbcUrl: jdbc:mysql://192.168.88.61:3306/xxxx
    username: root
    password: xxxxxxx
rules:
  - !READWRITE_SPLITTING
    dataSources:
      readwrite_ds:
        staticStrategy:
          writeDataSourceName: ds_1
          readDataSourceNames:
            - ds_2
        loadBalancerName: random
    loadBalancers:
      random:
        type: RANDOM
props:
  sql-show: true
```
注入示例：

```java
import org.noear.solon.annotation.Component;

@Configuration
public class Config {

    @Bean(name = "shardingDs1")
    DataSource shardingSphere2(@Inject("${sharding.demo1}") ShardingSphereSupplier supplier) throws Exception {
        return supplier.get();
    }
    
    @Bean(name = "shardingDs2", typed = true)
    DataSource shardingSphere(@Inject("${sharding.demo2}") ShardingSphereSupplier supplier) throws Exception {
        return supplier.get();
    }
    
}

@Component
public class UserService {
    @Db("shardingDs1")
    OrderMapper orderMapper;

    @Db("shardingDs2")
    UserMapper userMapper;
}
```