
配置示例::

```yaml
sharding-sphere:
  config:
    yml: 'classpath:sharding.yml'
```

然后在resources目录下，添加 sharding.yml 文件，内容如下：

其余配置：<a href="https://shardingsphere.apache.org/document/current/cn/user-manual/shardingsphere-jdbc/yaml-config/">官方链接</a>

```yaml
# Demo 读写分离配置
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

public class DemoController {
    @Db("shardingDs") //shardingDs datasource
    SqlSessionFactory factory;
    
    @Db("shardingDs") //shardingDs datasource
    SqlSession session;
    
    @Db("shardingDs") //shardingDs datasource
    UserMapper mapper;
}

```