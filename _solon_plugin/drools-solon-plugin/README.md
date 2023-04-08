## Usage
- 1）在`pom.xml`中引入依赖

  ```xml
  <dependency>
	    <groupId>org.noear</groupId>
	    <artifactId>drools-solon-plugin</artifactId>
	    <version>${solon.version}</version>
	</dependency>
  ```
  
- 2）在配置文件中指定规则文件的路径

  ```xml
  ################## 必填属性 ##################
  # 指定规则文件目录，会自动扫描该目录下所有规则文件，决策表，以及CSV文件
  # 支持classpath资源目录，如：classpath:drools/**/*.drl
  # win 系统注意使用反斜杠，如：C:\\DRL\\
  # linux 系统注意使用斜杠，如：/usr/local/drl/
  spring.drools.path = C:\\DRL\\
  ################## 可选属性 ##################
  # 也可以指定全局的mode，选择stream或cloud（默认stream模式）
  spring.drools.mode = stream
  # 自动更新，on 或 off（默认开启）
  spring.drools.auto-update = on
  # 指定规则文件自动更新的周期，单位秒（默认30秒扫描偶一次）
  spring.drools.update = 10
  # 规则监听日志，on 或 off（默认开启）
  spring.drools.listener = on
  # 开启 drl 语法检查，on 或 off（默认关闭）
  spring.drools.verify = off
  # 指定规则文件的字符集（默认 UTF-8）
  spring.drools.charset = GBK
  ```
  
- 3）使用注解方式引入KieTemplate

  ```java
  @Inject
  private KieTemplate kieTemplate;
  ```
  
- 4）使用 kieTemplate 的 getKieSession 方法，指定规则文件名，就可以获取对应的 Session，可以传入多个规则文件，包括决策表

  ```java
  KieSession kieSession = kieTemplate.getKieSession("rule1.drl", "rule2.drl");
  ......
  ```