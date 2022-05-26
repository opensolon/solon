# 支持 grallvm native-image 方式打包运行

### 打包前准备

#### 1、需要通过 agent 生成 graalvm 需要的配置文件

```shell
java -agentlib:native-image-agent=config-output-dir=./config/ -jar solondemo.jar
```

#### 2、执行后，最好能跑一跑 testcase，尽量保证代码覆盖率100%，避免打包后遇到 classnotfound 执行完成后 终止执行

#### 3、项目 resources 下创建 META-INF/native-image 目录，把上一步生成的 config 目录下的 6 个 json 文件 copy 进来

#### 4、修改 resource-config.json 文件 添加一条数据

```json
{
  "pattern":"META-INF/native-image/.*"
}
```

#### 5、重新打包

#### 6、编译成本地镜像即可运行

```shell
native-image  -jar solondemo.jar --allow-incomplete-classpath -H:+ReportExceptionStackTraces --enable-http
```

demo: https://gitee.com/mantouchong/solondemo