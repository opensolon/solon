
配置参考：

```yml
server.session:
  timeout: 300 #s
  state:
    jwt:
      name: TOKEN #变量名；（可不配，默认：TOKEN）
      secret: "E3F9N2kRDQf55pnJPnFoo5+ylKmZQ7AXmWeOVPKbEd8=" #密钥（使用 JwtUtils.createKey() 生成）；（可不配，默认：xxx）
      allowExpire: true #充许超时；（可不配，默认：true）
      allowIssue: true #充许输出；（可不配，默认：true）
      allowUseHeader: false #充许使用Header传递；（可不配，默认：使用 Cookie 传递）
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