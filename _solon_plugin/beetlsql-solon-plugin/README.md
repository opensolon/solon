


```yaml
test:
  db1:
    schema: rock
    jdbcUrl: jdbc:mysql://localhost:3306/rock?useUnicode=true&characterEncoding=utf8&autoReconnect=true&rewriteBatchedStatements=true
    driverClassName: com.mysql.cj.jdbc.Driver
    username: root
    password: 123456

beetlsql.db1:
  dbStyle: "org.beetl.sql.core.nosql.PrestoStyle"
  inters: 
    - "org.beetl.sql.ext.DebugInterceptor"
```