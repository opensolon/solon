
## Solon 应用常用配置说明

#### 一、服务端基本属性

  ```yaml
#服务的 文件编码
file.encoding: "utf-8"

#服务的 主端口（默认为8080）
server.port: 8080

#服务的 http 信号端口（默认为 ${server.port}）
server.http.port: 8080
#服务的 socket 信号端口（默认为 20000+${server.port}）
server.socket.port: 28080
#服务的 websocket 信号端口（默认为 10000+${server.port}）
server.websocket.port: 18080

#服务的 http 信号名称，服务注册时可以为信号指定名称（默认为 ${solon.app.name}）
server.http.name: "waterapi"
#服务的 socket 信号名称，服务注册时可以为信号指定名称（默认为 ${solon.app.name}）
server.socket.name: "waterapi.tcp"
#服务的 websocket 信号名称，服务注册时可以为信号指定名称（默认为 ${solon.app.name}）
server.websocket.name: "waterapi.ws"

#设定最大的请求包大小
server.request.maxRequestSize: 2Mb #kb,mb

#设定会话超时秒数（单位：秒）
server.session.timeout: 3600
#设定会话状态的cookie域（默认为当前域名）
server.session.state.domain: noear.org

#当使用 sesstionstate.redis 的配置
server.session.state.redis.server: redis.dev.zmapi.cn:6379
server.session.state.redis.password: AVsVSKd1
server.session.state.redis.db: 31
server.session.state.redis.maxTotaol: 200

```

  #### 二、应用基本属性

  ```yaml
#应用名称
solon.app.name: "waterapi"
#应用组
solon.app.group: "water"
#应用标题
solon.app.title: "WATER"

#应用扩展文件夹
solon.extend: "ext"
#应用扩展加载，隔离模式
solon.extend.isolation: true

#应用配置文件活动选择 # application-dev.properties 或 application-dev.yml
solon.profiles.active: dev

#应用元信息输出开启（输出每个插件的信息）
solon.output.meta: 1
  ```

  #### 三、调试模式控制
  ```yaml
solon.debug:
  enableCaching: false
  enableTransaction: true
  ```

  #### 四、视图后缀与模板引擎的映射配置
  ```yaml
#默认约定的配置（不需要配置，除非要修改）
solon.view.mapping.htm: BeetlRender #简写
solon.view.mapping.shtm: EnjoyRender
solon.view.mapping.ftl: FreemarkerRender
solon.view.mapping.jsp: JspRender
solon.view.mapping.html: ThymeleafRender

#添加自义定映射时，需要写全类名
solon.view.mapping.vm: org.noear.solon.view.velocity.VelocityRender #全名（一般用简写）
  ```

  #### 五、MIME映射配置
  ```yaml
#示例如下（solon.extend.staticfiles 组件已内置了一批；缺少的可手动配置）
solon.mime:
  json: "application/json"
  jpg: "image/jpeg"
  ```

  #### 六、安全停止插件的配置
  ```yaml
#安全停止的延时秒数
solon.stop.delay: 10

#是否启用安全停止插件
solon.stop.enable: false
#远程停止地址
solon.stop.path: /run/stop/
#充许调用远程停止的主机ip
solon.stop.host: 127.0.0.1
  ```

  #### 七、SocketD插件配置
  ```yaml
#读缓存大小
solon.socketd.readBufferSize: "1kb"
#写缓存大小
solon.socketd.writeBufferSize: "1kb"
#连接超时
solon.socketd.connectTimeout: "60s"
#Socket超时
solon.socketd.socketTimeout: "60s"
  ```

  #### 七、Staticfiles 插件配置
  ```yaml
#是否启用静态文件服务
solon.staticfiles.enable: true
#浏览器304缓存秒数（单位：秒）
solon.staticfiles.maxAge: 600
  ```

  #### 八、Cron4j 插件配置（cron4j-solon-plugin）

  ```yaml
solon.cron4j.job1:
  cron5x: "2s"  #快捷时间配置
  enable: true

solon.cron4j.job2:
  cron5x: "*/1 * * * *"  #cron时间配置模式，支持5段（分、时、天、月、周）
  enable: true
  ```

  #### 八、Quartz 插件配置（quartz-solon-plugin）

  ```yaml
solon.quartz.job1:
  cron7x: "2s"    #快捷时间配置
  enable: true

solon.quartz.job2:
  cron7x: "0 0/1 * * * * ?"  #cron时间配置模式，支持7段（秒、分、时、日、月、周、年）
  enable: true
  ```

  #### 九、Dubbo 插件配置（dubbo-solon-plugin）

  ```yaml
dubbo:
  scan:
    basePackages: "x.x.x.x"
  application:
    name: "hello-provider"
    owner: "noear"
  registry:
    address: "nacos://192.168.8.118:8848"
  ```

  #### 十、Solon Cloud 配置

  太多了，另起一份资料介绍




