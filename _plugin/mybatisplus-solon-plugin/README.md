# 简介 | Intro

Mybatis 增强工具包 for Solon - 让Solon使用体验更加舒适

# 示例 | Example

```yaml
# 配置数据源
dataSource:
  db1:
    # 与数据库名可用保持一致
    schema: rock
    jdbcUrl: jdbc:mysql://localhost:3306/rock?useUnicode=true&characterEncoding=utf8&autoReconnect=true&rewriteBatchedStatements=true
    driverClassName: com.mysql.cj.jdbc.Driver
    username: root
    password: 123456
mybatis:
  db1:
    typeAliases:
      - "webapp.model"    #支持包名
    mappers:
      - "webapp.dso.db1"            #或支持包名
      - "webapp/dso/db1/mapp.xml"   #或支持mapper xml
      - "webapp/dso/db1/mapp.class" #或支持mapper class (以 class 结尾)

# 配置插件
mybatis.plugin:
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
public class MybatisExtConfiguration implements EventListener<org.apache.ibatis.session.Configuration> {

    @Override
    public void onEvent(org.apache.ibatis.session.Configuration configuration) {
        //扩展
        //configuration.addInterceptor(...);
    }
}
```