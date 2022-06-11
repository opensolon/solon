
#### 配置示例

```properties
solon.health.detector=cpu,disk,jvm,memory,os,qps
```


```yml
# 可选: cpu,disk,jvm,memory,os,qps (qps 最好别加)
solon.health.detector: "cpu,jvm"
```

/healthz