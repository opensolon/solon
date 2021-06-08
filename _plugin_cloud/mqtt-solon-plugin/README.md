

#### 配置示例

```yaml
solon.app:
  group: demo       #配置服务使用的默认组
  name: helloapp    #发现服务使用的应用名

solon.cloud.mqtt:
  server: "tcp://localhost:41883" 
  event:
    cleint: "...." # 对 Client Connect Options 的参数配置（默认：不需要加）

```