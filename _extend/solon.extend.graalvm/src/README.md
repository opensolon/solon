# 支持grallvm native-image方式打包运行
- 打包前准备
 1、需要通过agent生成graalvm需要的配置文件
```
java -agentlib:native-image-agent=config-output-dir=./config/ -jar solondemo.jar
```
2、执行后，最好能跑一跑testcase，尽量保证代码覆盖率100%，避免打包后遇到 classnotfound执行完成后 终止执行
3、项目resources下创建META-INF/native-image目录，把上一步生成的 config目录下的6个json文件copy进来

**4、修改resource-config.json文件 添加一条数据**
```
    {
      "pattern":"META-INF/native-image/.*"
    }
```
5、重新打包

6、编译成本地镜像即可运行
```
native-image  -jar solondemo.jar --allow-incomplete-classpath -H:+ReportExceptionStackTraces --enable-http

```

demo: https://gitee.com/mantouchong/solondemo