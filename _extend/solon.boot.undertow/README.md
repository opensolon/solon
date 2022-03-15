

### 添加https证书支持

```shell
java -Djavax.net.ssl.keyStore=/demo.keystore 
     -Djavax.net.ssl.keyStorePassword=demo 
     -Djavax.net.ssl.keyStoreType=PKCS12
     -jar  demo.jar
```