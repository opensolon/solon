
配置参考：

```yml
server.session:
  timeout: 7200 #单位秒；（可不配，默认：7200）
  state:
    jwt:
      name: TOKEN #变量名；（可不配，默认：TOKEN）
      secret: "E3F9N2kRDQf55pnJPnFoo5+ylKmZQ7AXmWeOVPKbEd8=" #密钥（使用 JwtUtils.createKey() 生成）；（可不配，默认：xxx）
      prefix: Bearer #令牌前缀（可不配，默认：空）
      allowExpire: true #充许超时；（可不配，默认：true）；false，则token一直有效
      allowAutoIssue: true #充许自动输出；（可不配，默认：true）；flase，则不向header 或 cookie 设置值（由用户手动控制）
      allowUseHeader: false #充许使用Header传递；（可不配，默认：使用 Cookie 传递）；true，则使用 header 传递
```

生成密钥：

```java
public class JwtTest {
    @Test
    public void test(){
        System.out.println(JwtUtils.createKey());
    }
}
```