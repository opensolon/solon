

### 添加https证书支持

通过应用配置文件（app.yml）进行配置

```yml
server:
  ssl:
    keyStore: "/demo.jks" #或者应用内资源文件 "demo.jks"
    keyPassword: "demo"
    

```

通过启动命令增加配置

```shell
java -Dserver.ssl.keyStore=/demo.jks 
     -Dserver.ssl.keyPassword=demo 
     -jar  demo.jar
```