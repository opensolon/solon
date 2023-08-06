
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

如果没有提供 accessKey 和 secretKey，则使用所提供的默认凭证（需要在AWS服务上运行，并使用AWS IAM身份验证）

```yaml
solon.cloud.aws.s3:
  file:
    enable: true                  #需要显示启用
```