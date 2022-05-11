
配置示例::

```yaml
# 配置数据源
mybatis.db1:
  typeAliases:
    - "webapp.model"    #支持包名
  mappers:
    - "webapp.dso.db1"            #或支持包名
    - "webapp/dso/db1/mapp.xml"   #或支持mapper xml 资源地址
    - "webapp/dso/db1/*.xml"      #或支持mapper xml 资源地址
    - "webapp/dso/db1/mapp.class" #或支持mapper class 资源地址(以 class 结尾)      

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