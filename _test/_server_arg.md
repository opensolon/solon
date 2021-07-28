
#### 一、服务端启动参数使用

* 1.通过参数配置，例：

```shell
java -jar xxx.jar --debug=1 --env=pro
```

* 2.通过系统属性变量配置，例：

```shell
java -Dsolon.debug=1 -Dsolon.env=pro -jar xxx.jar
```


* 3.通过Docker环境变量配置，例：

```shell
docker run -e solon.debug=1 -e solon.env=pro
docker run -e JAVA_OPTS='-Dsolon.debug=1 -Dsolon.env=pro'
```

#### 二、服务端启动参数说明

| 启动参数风格 | 属性配置风格 | 说明 | 示例 |
| -------- | -------- | -------- | -------- |
| env     | solon.env     | 环境     |  --env=pro     |
|      |      |      |      |
| debug     | solon.debug     | 调试模式(默认为0)      | --debug=1    |
| setup     | solon.setup     | 安装模式(默认为0)      | --setup=1    |
| white     | solon.white     | 白色模式(默认为1)      | --white=1    |
| drift     | solon.drift     | 漂移模式(默认为0)      | --drift=1    |
|      |      |      |      |
| extend            | solon.extend            | 扩展目录        | --extend=jt_ext (相对位置) 或  --extend=/data/sss/jt_ext   (绝对位置)    |
| extend.filter     | solon.extend.filter     | 扩展目录过滤     | --extend.filter=.yml,hbase     |
|      |      |      |      |
| app.name      | solon.app.name      | 应用名(用字母或数字或-)     | --app.name=demoapp    |
| app.group     | solon.app.group     | 应用分组(用字母或数字-)     | --app.group=demo      |
| app.title     | solon.app.title     | 应用标题或显示名           | --app.title=演示项目    |

#### 注：其它属性配置都可通过 系统属性设置 或  docker 环境变量设置实现