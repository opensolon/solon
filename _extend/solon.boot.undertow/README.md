

### 添加https证书支持

```yml
server:
  ssl:
    keyStore: "/demo.jks"
    keyPassword: "demo"
    

```

```shell
java -Dserver.ssl.keyStore=/demo.jks 
     -Dserver.ssl.keyPassword=demo 
     -jar  demo.jar
        
#或者  

java -Djavax.net.ssl.keyStore=/demo.pfx 
     -Djavax.net.ssl.keyStorePassword=demo 
     -jar  demo.jar
```