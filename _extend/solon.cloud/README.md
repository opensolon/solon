### 一、配置示例：

```yaml
solon:
  app:
    name: "solon-consul-test"
    group: "test"

solon.cloud.consul:
  server: "localhost"

```

### 二、配置说明

注：具体配置时，用框架名替代@@符

| 属性说 | 说明 | 框架可使用情况 | 
| -------- | -------- |  -------- |  
| solon.cloud.@@.server     |   服务地址（ip:port）   |    nacos、consul、water  |  
| solon.cloud.@@.token     |   链接令牌   |    consul   |  
| solon.cloud.@@.username     |  链接用户名    |  nacos    |  
| solon.cloud.@@.password     |  链接密码   |    nacos  |    
| solon.cloud.@@.alarm     |  造警接收号   |    water  |  
| | | |
| solon.cloud.@@.config.enable     |   配置服务启用（默认：true）   |   nacos、consul、water     |   
| solon.cloud.@@.config.server     |   服务地址（ip:port）   |   nacos、consul、water     | 
| solon.cloud.@@.config.load     |  需要启动时加载的配置    |    nacos、consul、wate   |    
| solon.cloud.@@.config.refreshInterval     |  配置刷新间隔    |   consul    |     
| | | |
| solon.cloud.@@.discovery.enable     |    注册与发现服务启用（默认：true）   |     nacos、consul、water    | 
| solon.cloud.@@.discovery.server     |   服务地址（ip:port）   |   nacos、consul、water     | 
| solon.cloud.@@.discovery.tags     |  服务标签    |    consul    |  
| solon.cloud.@@.discovery.healthCheckPath     |  服务健康检查路径    |    consul、water    |     
| solon.cloud.@@.discovery.healthCheckInterval     |  服务健康检查间隔时间    |    consul    |    
| solon.cloud.@@.discovery.healthDetector     |   服务健康上报指标   |   consul     |     
| solon.cloud.@@.discovery.refreshInterval     |   服务发现刷新间隔   |   consul     |      
| | | |
| solon.cloud.@@.event.enable | 事件总线服务（默认：true）| water、rabbitmq、rocketmq |
| solon.cloud.@@.event.server     |   服务地址（ip:port）   |  water、rabbitmq、rocketmq     | 
| solon.cloud.@@.event.exchange     |   交换机或关系组   |  rabbitmq、rocketmq     | 
| solon.cloud.@@.event.queue     |   指定队列   |  rabbitmq、rocketmq     | 
| solon.cloud.@@.event.receive     |   指定接收域   |  water     | 
| solon.cloud.@@.event.seal | 事件签名 | water |
| | | |
| solon.cloud.@@.lock.enable | 分布式锁服务（默认：true）| water |
| solon.cloud.@@.lock.server     |   服务地址（ip:port）   |   water     | 
| | | |
| solon.cloud.@@.log.enable | 日志总线服务（默认：true）| water |
| solon.cloud.@@.log.server     |   服务地址（ip:port）   |   water     | 
| solon.cloud.@@.log.default | 日志默认记录器 | water |




### 三、适配要求

#### （一）日志服务适配要求

* 对业务的性能影响极小
  * 例如：消息先进入本地队列，累积后再批量提交到服务端

#### （二）事件服务适配要求

* 支持定时事件
* 只要订阅了主题，各服务组都能收到
* 当前处理失败后逐级延后重试，直到最终成功；但不影响别的服务组

延后间隔如下（可通过ExpirationUtils生成ttl）：

| 次数 | 延后间隔时间 |
| --- | --- |
| 0 | 0 |
| 1 | 5s |
| 2 | 10s |
| 3 | 30秒 |
| 4 | 1分钟 |
| 5 | 2分种 |
| 6 | 5分钟 |
| 7 | 10分钟 |
| 8 | 30分钟 |
| 9 | 1小时 |
| n.. | 2小时 |
