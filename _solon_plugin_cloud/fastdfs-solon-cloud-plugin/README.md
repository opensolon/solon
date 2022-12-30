# fastdfs-solon-cloud-plugin

调整说明： 

* 改用 "solon.cloud.fastdfs" 做为配置前缀
* 取消原 "confPath" 配置；且约定 "fastdfs.properties" 或 "fastdfs.yml" 做为配置文件
* 取消原 "groupName" 配置；由标准配置 "bucket" 替代
* 拟添加 "fastdfs_def.properties"，以支持标准的快捷配置

## 插件配置

```yaml
solon.cloud.fastdfs:
  file:
    enable: true             #是否启用（默认：启用）
    bucket: "group1"         # group 名称
    endpoint: "10.0.11.201:22122,10.0.11.202:22122"
    secretKey: "FastDFS1234567890"
```

## FastDFS 配置文件

** fastdfs.properties **

```properties
fastdfs.charset=UTF-8

fastdfs.connect_timeout_in_seconds=5
fastdfs.network_timeout_in_seconds=30

fastdfs.http_anti_steal_token=false
fastdfs.http_secret_key=FastDFS1234567890
fastdfs.http_tracker_http_port=80

fastdfs.tracker_servers=10.0.11.201:22122,10.0.11.202:22122,10.0.11.203:22122

fastdfs.connection_pool.enabled=true
fastdfs.connection_pool.max_count_per_entry=500
fastdfs.connection_pool.max_idle_time=3600
fastdfs.connection_pool.max_wait_time_in_ms=1000
```

> https://github.com/happyfish100/fastdfs
> https://github.com/happyfish100/fastdfs-client-java