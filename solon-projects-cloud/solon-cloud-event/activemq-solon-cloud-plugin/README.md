

#### 配置示例

```yaml
solon.app:
  group: demo       #配置服务使用的默认组
  name: helloapp    #发现服务使用的应用名

solon.cloud.activemq:
  server: "localhost:61616"    #服务地址
  username: root              #链接账号
  password: 123456            #链接密码

```
注意：activemq不支持分组 ,本插件对接的是activemq5

