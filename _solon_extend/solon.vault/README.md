
使用说明：

#### 1、添加配置

```yaml
solon.guard:
  password: "liylU9PhDq63tk1C"
```

也可以通过运行时添加：`java -Dsolon.guard.password=liylU9PhDq63tk1C -jar demoapp.jar`

#### 2、使用工具生成密文

```java
public class TestApp {
    public static void main(String[] args) throws Exception{
        Solon.start(TestApp.class, args);

        //打印生成的密文
        System.out.println(GuardUtils.encrypt("root"));
    }
}
```

#### 3、使用生产的密文，进行敏感数据的配置

```yaml
solon.guard:
  password: "liylU9PhDq63tk1C"

test.db1:
  url: ""
  username: "ENC(xo1zJjGXUouQ/CZac55HZA==)"
  password: "ENC(XgRqh3C00JmkjsPi4mPySA==)"
```


使用 `@VaultInject` 做密文配置的注入:

```java
@Configuration
public class TestConfig {
    @Bean("db2")
    private DataSource db2(@VaultInject("${test.db1}") HikariDataSource ds){
        return ds;
    }
}
```