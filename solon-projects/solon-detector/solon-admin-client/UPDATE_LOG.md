
2023-07-10

* 取消 solon-web, solon.boot.undertow 依赖（由主程序去添加，用户可以自由选择）
* 取消 okhttp 4.x 的依赖，改成 3.x （4.x 包加大了很多，没必要）
* 取消 gson 的依赖
* 添加插件集成配置（否则，别的项目扫描不到 MonitorController）