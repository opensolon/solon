# fastdfs-solon-cloud-plugin

## 插件配置

```yaml
solon.cloud.file.fastdfs:
  confPath: fdfs_client.conf  # fastdfs 配置文件路径
  groupName: group1           # group 名称
```

## FastDFS 配置文件

支持两种格式的配置文件

**conf格式**

```text
connect_timeout = 2
network_timeout = 30
charset = UTF-8
http.tracker_http_port = 80
http.anti_steal_token = no
http.secret_key = FastDFS1234567890

tracker_server = 10.0.11.247:22122
tracker_server = 10.0.11.248:22122
tracker_server = 10.0.11.249:22122

connection_pool.enabled = true
connection_pool.max_count_per_entry = 500
connection_pool.max_idle_time = 3600
connection_pool.max_wait_time_in_ms = 1000
```

**properties格式**

```properties
fastdfs.connect_timeout_in_seconds=5
fastdfs.network_timeout_in_seconds=30
fastdfs.charset=UTF-8
fastdfs.http_anti_steal_token=false
fastdfs.http_secret_key=FastDFS1234567890
fastdfs.http_tracker_http_port=80

fastdfs.tracker_servers=10.0.11.201:22122,10.0.11.202:22122,10.0.11.203:22122

fastdfs.connection_pool.enabled=true
fastdfs.connection_pool.max_count_per_entry=500
fastdfs.connection_pool.max_idle_time=3600
fastdfs.connection_pool.max_wait_time_in_ms=1000
```

>https://github.com/happyfish100/fastdfs
>https://github.com/happyfish100/fastdfs-client-java