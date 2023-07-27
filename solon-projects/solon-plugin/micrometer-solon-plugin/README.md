# micrometer-solon-plugin

#### 介绍
solon的micrometer-solon-plugin插件

#### 快速入门

```
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-{prometheus}</artifactId>
    <version>${micrometer.version}</version>
    <exclusions>
        <exclusion>
            <artifactId>micrometer-core</artifactId>
            <groupId>io.micrometer</groupId>
        </exclusion>
    </exclusions>
</dependency>
```
1.描述
基础扩展插件，为solon提供一个暴露站点,提供给micrometer进行数据分析。内置了一个计数器和计时器，为接口提供服务

```java
@Configuration
public class MyPrometheusMeterRegistry extends AbsMeterRegistry<PrometheusMeterRegistry> {


    public MyPrometheusMeterRegistry() {
        super(new PrometheusMeterRegistry(PrometheusConfig.DEFAULT));
    }

    /**
     *  注册器消息体内容
     *
     * @param prometheusMeterRegistry 普罗米修斯计注册表
     * @return {@link String}x
     */
    @Override
    public String scrape(PrometheusMeterRegistry prometheusMeterRegistry) {
        return prometheusMeterRegistry.scrape(TextFormat.CONTENT_TYPE_OPENMETRICS_100);
    }

    /**
     * 注册表
     *
     * @param meterRegistry 计注册表
     */
    @Override
    public void registry(MeterRegistry meterRegistry){
        // 全局注册
        super.registry(meterRegistry);
    }
}
```
2.配置参考
prometheus.yml
```yml
scrape_configs:
  - job_name: 'mf-micrometer-example'
    scrape_interval: 5s
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['127.0.0.1:8080']
        labels:
           instance: 'mf-example'
```

3.访问地址
http://localhost:8080/actuator/prometheus

![img_1.png](img_1.png)

4.搭配prometheus使用如下
![img_2.png](img_2.png)
