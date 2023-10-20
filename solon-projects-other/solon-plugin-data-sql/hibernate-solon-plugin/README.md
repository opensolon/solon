

这个插件的适配分为两部分：


### 1、原生 jpa 接口

* 主要由添加个 JpaPersistenceProvider 实现。从而避开需要 xml 文件（也还可以有 hibernate 提供者支持）

```java
PersistenceProviderResolverHolder
                .getPersistenceProviderResolver()
                .getPersistenceProviders()
                .add(new JpaPersistenceProvider());
```

* 通过 JpaTranSessionFactory 代理，与 Solon 事务完成对接

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
jpa.db1:
  mappings:
    - org.example.entity.*
  properties:
    hibernate:
      hbm2ddl:
        auto: create
      show_sql: true
      format_sql: true
      dialect: org.hibernate.dialect.MySQL8Dialect
      connection:
        isolaction: 4 # 事务隔离级别 4 可重复度
```

* 构建数据源

```java
//配置数据源
@Configuration
public class Config {
    //此下的 db1 与 beetlsql.db1 将对应在起来 //可以用 @Db("db1") 注入mapper
    //typed=true，表示默认数据源。@Db 可不带名字注入 
    @Bean(name="db1", typed=true)
    public DataSource db1(@Inject("${demo.db1}") HikariDataSource ds) {
        return ds;
    }
}
```

* 应用

```java
@Mapping("jpa")
@Controller
public class JapController {
    @Db
    private EntityManagerFactory entityManagerFactory;

    private EntityManager openSession() {
        return entityManagerFactory.createEntityManager();
    }

    @Tran
    @Mapping("/t")
    public void t1() {
        HttpEntity entity = new HttpEntity();
        entity.setId(System.currentTimeMillis() + "");

        openSession().persist(entity);

    }

    @Mapping("/t2")
    public Object t2() {
        HttpEntity entity = new HttpEntity();
        entity.setId(System.currentTimeMillis() + "");

        return openSession().find(HttpEntity.class, "1");
    }
}
```

