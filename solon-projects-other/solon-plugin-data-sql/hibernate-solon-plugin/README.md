

这个插件的适配分为两部分：


### 1、原生 jpa 接口

* 主要由添加个 SolonPersistenceProvider 实现。从而避开需要 xml 文件（也还可以有 hibernate 提供者支持）

```java
PersistenceProviderResolverHolder
                .getPersistenceProviderResolver()
                .getPersistenceProviders()
                .add(new SolonPersistenceProvider());
```

* 通过 SessionFactoryProxy 代理，与 Solon 事务完成对接

### 2、使用示例

* 配置

```yaml
#solon 支持的多数据源
test.db1:
  schema: rock
  jdbcUrl: jdbc:mysql://121.40.62.167:3306/test?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8&autoReconnect=true&rewriteBatchedStatements=true
  driverClassName: com.mysql.cj.jdbc.Driver
  username: bai
  password: root

#db test的hibernate配置
jpa.test:
  mapping:
    - org.example.entity.*
  hibernate:
    hbm2ddl:
      auto: create
    show_sql: true
    format_sql: true
    dialect: org.hibernate.dialect.MySQL8Dialect
    connection:
      isolaction: 4 # 事务隔离级别 4 可重复度
```

