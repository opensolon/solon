

```yaml
# solon.scheduling.job.{job name} #要控制的job需要设置name属性
#
solon.scheduling.job.job1:
  cron: "* * * * * ?"  #重新定义调度表达式
  zone: "+08"
  fixedRate: 0
  fixedDelay: 0
  initialDelay: 0
  enable: true #用任务进行启停控制
```