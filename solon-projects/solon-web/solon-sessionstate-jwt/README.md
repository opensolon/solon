
配置参考：

```yml
server.session:
  timeout: 7200 #单位秒；（可不配，默认：7200）
  state:
    jwt:
      name: TOKEN #变量名；（可不配，默认：TOKEN）
      #secret: "your-base64-secret" #密钥（使用 JwtUtils.createKey() 生成）；（可不配，默认保存到运行 Solon 进程的操作系统用户 home 目录下的 ~/.solon/settings.json）
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

如果不配置 `server.session.state.jwt.secret`，Solon 会首次启动时生成随机密钥，并将其保存到运行 Solon 进程的操作系统用户 home 目录下的 `~/.solon/settings.json`。该文件使用 Map 结构保存设置，例如：

```json
{
  "server.session.state.jwt.secret": "generated-secret"
}
```

后续启动会复用已保存的密钥；显式配置的 `server.session.state.jwt.secret` 优先级更高。
