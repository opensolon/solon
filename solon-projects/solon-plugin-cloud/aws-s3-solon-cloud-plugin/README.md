
#### 配置示例

aws

```yaml
solon.cloud.aws.s3:
  file:
    enable: true                  #是否启用（默认：启用）
    bucket: "demo_bucket"
    accessKey: "ak..."
    secretKey: "sk..."
    regionId: "ap-southeast-1"
```

other

```yaml
solon.cloud.aws.s3:
  file:
    enable: true                  #是否启用（默认：启用）
    endpoint: 'https://obs.cn-southwest-2.myhuaweicloud.com' #通过协议，表达是否使用 https?
    regionId: ''
    accessKey: 'xxxx'
    secretKey: 'xxx'  
```

If not provided accessKey and secretKey, then use aws default credentials provided. 
you need run on AWS services and use AWS IAM authentications.

```yaml
solon.cloud.aws.s3:
  file:
    enable: true                  #是否启用（默认：启用）
```