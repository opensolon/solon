local-solon-cloud-plugin 是 solon cloud 标准的本地模拟实现。（src/test，提供了所有能力的演示）：

场景定位：
* 统一使用 solon cloud 接口进行项目开发
* 只需要切换配置；就可以在 "本地单体服务" 和 "分布式服务（或微服务）"之间自由切换
* 比如
  * 定时任务可以在本地调度，配置改动就可以由 water 或 xxl-job 进行调度
  * 或者配置，自由在本地配置与云端配置之间切换

能力说明：

### 1、云端配置服务（本地模拟）

内容格式支持： yml, properties, json （后缀做为name的一部分，可有可无）<br/>
文件地址格式： META-INF/solon-cloud/config@{group}_{name}，例示：

* META-INF/solon-cloud/config@demo_demo-db
* META-INF/solon-cloud/config@demo_demoapp.yml


**两种应用：**

可以通过配置加载配置
```yaml
solon.cloud.local:
  config:
    load: "demoapp.yml"
```
可以通过注解直接注入
```java
@Configuration
public class Config {
    @Bean
    public void init1(@CloudConfig("demo-db") Properties props) {
        System.out.println("云端配置服务直接注入的：" + props);
    }
}
```

### 2、云端注册与发现服务（本地模拟）

让服务注册有地方去，也有地方可获取（即发现）

### 3、云端事件服务（本地模拟）

本地摸拟实现。不支持ACK，不支持延时。最好还是引入消息队列的适配框架

### 4、云端国际化配置服务（本地模拟）

内容格式支持： yml, properties, json （不能有手缀名，为了更好的支持中文）<br/>
文件地址格式： META-INF/solon-cloud/i18n@{group}_{name}-{locale}，例示：

* META-INF/solon-cloud/i18n@demo_demoapp-zh_CN
* META-INF/solon-cloud/i18n@demo_demoapp-en_US


### 5、云端定时任务调度服务（本地模拟）

时间到就会启动新的执行（不管上次是否执行完成了）


### 6、云端名单服务（本地模拟）

内容格式支持： json <br/>
文件地址格式： META-INF/solon-cloud/list@{name}-{type}.json，例示：

* META-INF/solon-cloud/list@whitelist-ip.json


### 7、云端度量服务（本地模拟）

一个空服务。只为已有调用不出错