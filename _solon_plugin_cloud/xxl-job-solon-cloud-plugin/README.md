
#### 配置示例

```yml
solon.app:
  group: demo       #配置服务使用的默认组
  name: helloapp    #发现服务使用的应用名

solon.cloud.xxljob:
  server: "http://localhost:8093/xxl-job-admin"


### xxl-job admin address list, （默认：${solon.cloud.xxljob.server}）
#xxl.job.admin.addresses: "${solon.cloud.xxljob.server}"
### xxl-job access token （默认：${solon.cloud.xxljob.token}）
#xxl.job.accessToken:
### xxl-job executor appname（默认：${solon.app.name}）
#xxl.job.executor.appname:
### xxl-job executor registry-address: 默认使用 address 注册 , 没有配置则用 ip:port（建议用 ip:port）
#xxl.job.executor.address:
### xxl-job executor server-info（默认：本机内网ip）
#xxl.job.executor.ip:
### xxl-job executor server-info（默认：9999）
#xxl.job.executor.port: 9999
### xxl-job executor log-path（默认：/data/logs/xxl-job/jobhandler）
#xxl.job.executor.logpath:
### xxl-job executor log-retention-days（默认：30）
#xxl.job.executor.logretentiondays: 30
```
