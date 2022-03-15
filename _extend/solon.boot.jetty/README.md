

### 添加https证书支持

```yml
server:
  ssl:
    keyStore: "/demo.keystore"
    keyPassword: "demo"
    keyType: "PKCS12"
    

```

```shell
java -Dserver.ssl.keyStore=/demo.keystore 
     -Dserver.ssl.keyPassword=demo 
     -Dserver.ssl.keyType=jks
     -jar  demo.jar
        
#或者  

java -Djavax.net.ssl.keyStore=/demo.keystore 
     -Djavax.net.ssl.keyStorePassword=demo 
     -Djavax.net.ssl.keyStoreType=PKCS12
     -jar  demo.jar
```