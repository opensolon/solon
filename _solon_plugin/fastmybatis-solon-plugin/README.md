# 简介 | Intro

[fastmybatis](https://gitee.com/durcframework/fastmybatis) for Solon - 让Solon使用体验更加舒适

# 示例 | Example

```yaml
server:
  port: 6041

# 配置数据源
dataSource:
  db1:
    # 与数据库名可用保持一致
    schema: stu
    jdbcUrl: jdbc:mysql://localhost:3306/stu?useUnicode=true&characterEncoding=utf8&autoReconnect=true&rewriteBatchedStatements=true
    driverClassName: com.mysql.cj.jdbc.Driver
    username: root
    password: root

mybatis:
  db1:
    mappers:
      - "com.myapp.demo.dao.*"
      # 指定mybatis xml文件存放目录
      - "classpath:mybatis/mapper/*.xml"
    configuration:
      cacheEnabled: false
      mapUnderscoreToCamelCase: true

```

注入示例：

```java

@Controller
public class DemoController {

    @Db("db1")
    TUserMapper mapper;

    /**
     * http://localhost:6041/index
     *
     * @return
     */
    @Mapping("index")
    public TUser index() {
        return mapper.getById(6);
    }

    /**
     * http://localhost:6041/index2
     *
     * @return
     */
    @Mapping("index2")
    public List<TUser> index2() {
        Query query = new Query()
                .in("id", Arrays.asList(4, 5, 6));
        return mapper.list(query);
    }
}
```

完整示例请查看本项目测试用例
