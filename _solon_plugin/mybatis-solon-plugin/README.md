
配置示例::

```yaml
# 配置数据源
mybatis.db1:
  typeAliases:
    - "webapp.model"    #支持包名
  mappers:
    - "webapp.dso.db1"            #或支持包名(要求 xml 同包同名)
    - "webapp/dso/db1/mapp.class" #或支持mapper class 资源地址(要求 xml 同包同名)
    - "mybatis/db1/mapp.xml"      #或支持mapper xml 资源地址
    - "mybatis/db1/*.xml"         #或支持mapper *.xml 资源地址     

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