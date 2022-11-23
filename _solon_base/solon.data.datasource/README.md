
DynamicDataSource 使用示例

配置示例（致少要有：type, strict, default）

* type: 设定数据源类型
* strict: 设定严格模式（默认：false）。启用后在未匹配到指定数据源时候会抛出异常, 不启用则使用默认数据源.
* default: 动态数据源的默认源

```yaml
demo.ds.db_user:
  type: "com.alibaba.druid.pool.DruidDataSource" 
  strict: true
  default: 
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

代码示使你

```java

```