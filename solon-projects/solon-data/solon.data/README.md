
动态数据源（或数据源集合）

```yaml
demo.ds.db_user:
  type: "com.alibaba.druid.pool.DruidDataSource"
  default: #动态数据源的默认源
    url: "xxx"
    username: "xxx"
    paasword: "xxx"
    driverClassName: "xx"
  db_user_2:
    url: "xxx"
    username: "xxx"
    paasword: "xxx"
    driverClassName: "xx"
```