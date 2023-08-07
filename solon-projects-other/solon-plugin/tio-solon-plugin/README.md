# tio-solon-plugin

# 最新版的tio是采用jdk17编译的，不建议在jdk8下运行

## 1、tio 用法

[t-io发展简史 - 谭聊 (tiocloud.com)](https://www.tiocloud.com/doc/tio/85?pageNumber=1)

- 1）在`pom.xml`中引入依赖

  ```xml
  <dependency>
	    <groupId>org.noear</groupId>
	    <artifactId>tio-solon-plugin</artifactId>
	    <version>1.0.1</version>
  </dependency>
  ```
  
- 2）在配置文件中配置tioserverconfig

  ```properties
  tio:
    core:
      server:
        name: tio_test
        # websocket port default 9876
        port: 6789
        heartbeat: true
        # 心跳时间
        heartbeat-timeout: 60000
        ip: 127.0.0.1
        #ip状态监视时间
        ip-stat-durations:
          - 60
        # SSL 配置
      ssl:
        key-store: #如果是以"classpath:"开头，则从classpath中查找，否则视为普通的文件路径
        password: #如果是以"classpath:"开头，则从classpath中查找，否则视为普通的文件路径
        trust-store: #密钥
  ```
  
- 3）使用注解方式引入TioServerConfig （可选）

  ```java
  @Inject
  private TioServerConfig tioServerConfig;
  ```
  
- 4）需要定义好 应用层Packet（Packet是用于表述业务数据结构的，我们通过**继承**Packet来实现自己的业务数据结构，对于各位而言，把Packet看作是一个普通的VO对象即可。）

- ```
  public class HelloPacket extends Packet {
  	private static final long serialVersionUID = -172060606924066412L;
  	public static final int HEADER_LENGTH = 4;//消息头的长度
  	public static final String CHARSET = "utf-8";
  	private byte[] body;
  
  	/**
  	 * @return the body
  	 */
  	public byte[] getBody() {
  		return body;
  	}
  
  	/**
  	 * @param body the body to set
  	 */
  	public void setBody(byte[] body) {
  		this.body = body;
  	}
  }
  ```
  
  4）需要定义好 TioHandler是处理消息的核心接口；TioListener是处理消息的核心接口；IpStatListener是可选，用于统计IP通道；GroupListener是可选，用于通道统计
  
  可以参考：[tio-core-spring-boot-starter - 谭聊 (tiocloud.com)](https://www.tiocloud.com/354?pageNumber=1)
  
  [_solon_plugin2/tio-solon-plugin/src/test · noear/solon - 码云 - 开源中国 (gitee.com)](https://gitee.com/noear/solon/tree/dev/_solon_plugin2/tio-solon-plugin/src/test)
  
  ```java
  //参考src/test/java 下的demo1包和org.tio.client.demo1 //这是定义client处理的
  package demo1;
  package org.tio.client.demo1;
  
  //配置文件参考 src/test/resources 下的app.yml
  ```