
#### 配置示例

```yml
solon.app:
  group: demo       #配置服务使用的默认组
  name: helloapp    #发现服务使用的应用名

solon.cloud.xxljob:
  server: "http://localhost:8093/xxl-job-admin"


### xxl-job admin address list, （默认：${solon.cloud.xxljob.server}）
#xxl.job.admin.addresses: "http://172.168.0.163:30126/xxl-job-admin"
### xxl-job access token （默认：${solon.cloud.xxljob.password}）
#xxl.job.accessToken:
### xxl-job executor appname（默认：${solon.app.name}）
#xxl.job.executor.appname:
### xxl-job executor registry-address: default use address to registry , otherwise use ip:port if address is null
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
