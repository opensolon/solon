支持 solon cloud 能力（src/test，提供了所有能力的演示）：


### 1、云端配置服务（本地模拟）

内容格式支持： yml, properties, json （后缀做为name的一部分，可有可无）<br/>
文件地址格式： META-INF/solon-cloud/config@{group}:{name}，例示：

* META-INF/solon-cloud/config@demo:demo-db
* META-INF/solon-cloud/config@demo:demoapp.yml



### 2、云端事件服务（本地模拟）

本地摸拟实现。不支持ACK，不支持延时。最好还是引入消息队列的适配框架

### 3、云端国际化配置服务（本地模拟）

内容格式支持： yml, properties, json （不能有手缀名，为了更好的支持中文）<br/>
文件地址格式： META-INF/solon-cloud/i18n@{group}:{name}-{locale}，例示：

* META-INF/solon-cloud/i18n@demo:demoapp-zh_CN
* META-INF/solon-cloud/i18n@demo:demoapp-en_US


### 4、云端定时任务调度服务（本地模拟）

时间到就会启动新的执行（不管上次是否执行完成了）


### 5、云端名单服务（本地模拟）

内容格式支持： json <br/>
文件地址格式： META-INF/solon-cloud/list@{name}-{type}.json，例示：

* META-INF/solon-cloud/list@whitelist-ip.json


### 6、云端度量服务（本地模拟）

一个空服务。只为已有调用不出错