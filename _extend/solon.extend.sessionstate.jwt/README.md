
配置参考：

```yml
server.session:
  timeout: 300 #s
  state:
    jwt:
      name: Token #变量名
      secret: "E3F9N2kRDQf55pnJPnFoo5+ylKmZQ7AXmWeOVPKbEd8=" #密钥（使用 JwtUtils.createKey() 生成）
      allowExpire: true #充许超时
      allowOutput: true #充许输出
      allowUseHeader: false #充许使用Header传递（默认为 Cookie）
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