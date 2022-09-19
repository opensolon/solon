


```yaml
test:
  db1:
    schema: rock
    jdbcUrl: jdbc:mysql://localhost:3306/rock?useUnicode=true&characterEncoding=utf8&autoReconnect=true&rewriteBatchedStatements=true
    driverClassName: com.mysql.cj.jdbc.Driver
    username: root
    password: 123456

# 配置印射的是 SQLManagerBuilder 字段（1.10.3 开始支持）
beetlsql.db1:
  dialect: "mysql"
  slaves: "db2,db3"
  debug: true
  dbStyle: "org.beetl.sql.core.db.MySqlStyle" #与 dialect 效果相同
  interceptorList: 
    - "org.beetl.sql.ext.DebugInterceptor" #与 debug 效果相同
```