
配置示例::

```yaml
# 配置数据源
demo.db1:
  # 与数据库名可用保持一致
  schema: rock
  jdbcUrl: jdbc:mysql://localhost:3306/rock?useUnicode=true&characterEncoding=utf8&autoReconnect=true&rewriteBatchedStatements=true
  driverClassName: com.mysql.cj.jdbc.Driver
  username: root
  password: 123456

mybatis.db1:
  typeAliases:  #支持包名 或 类名 //支持 ** 和 *
    - "webapp.model.*"
  mappers:      #支持包名 或 类名 或 xml（.xml结尾） //支持 ** 和 *
    - "webapp.dso.db1.*"
    - "webapp.dso.db1.DemoMapper"
    - "classpath:mybatis/dso/db1/demoMapper.xml"
    - "classpath:mybatis/**/db1/*.xml"     

# 配置全局插件
mybatis.plugins:
    - pagehelper:
        class: com.github.pagehelper.PageHelper
        dialect: mysql
        rowBoundsWithCount: true
```

注入示例：

```java

public class DemoController {
    @Db("db1") //db1 datasource
    SqlSessionFactory factory;
    
    @Db("db1") //db1 datasource
    SqlSession session;
    
    @Db("db2") //db2 datasource
    UserMapper mapper;
}

```

扩展插件示例：

```java
@Configuration
public class MybatisExtConfiguration {

    //调整 db1 的配置，或添加插件
    @Bean
    public void db1_cfg(@Db("db1") org.apache.ibatis.session.Configuration cfg) {
        //扩展
        //cfg.addInterceptor(...);
        cfg.setCacheEnabled(false);
    }
}
```