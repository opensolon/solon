<h1 align="center">Minio for solon</h1>

<div align="center">
Author noear，iYarnFog
</div>

## ✨ 特性

- 🌈 无厂商捆绑，免除后顾之忧
- 📦 开箱即用的高质量组件。

## 📦 安装

```xml
<dependency>
    <groupId>org.noear</groupId>
    <artifactId>minio-solon-plugin</artifactId>
</dependency>
```

## ⚙️ 配置

```yaml
solon:
  cloud:
    minio:
      file:
        enable: true                  #是否启用（默认：启用）
        endpoint: 'https://play.min.io'
        regionId: 'us-west-1'
        bucket: 'asiatrip'
        accessKey: 'Q3AM3UQ867SPQQA43P2F'
        secretKey: 'zuf+tfteSlswRu7BJ86wekitnifILbZam1KYY3TG'
```

## 🔨 示例

```java
//常规使用
public class DemoApp {
    public void main(String[] args) {
        SolonApp app = Solon.start(DemoApp.class, args);

        String key = "test/" + Utils.guid();
        String val = "Hello world!";

        //上传媒体
        Result rst = CloudClient.file().put(key, new Media(val));

        //获取媒体，并转为字符串
        String val2 = CloudClient.file().get(key).bodyAsString();
    }
}

//这样，可以获取其原始接口
MinioClient client = ((CloudFileServiceMinioImp)CloudClient.file()).getMinio();
```