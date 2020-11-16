
## Solon 启动处理顺序

* 1.实例化 Solon.global() 并加载配置
* 2.加载扩展文件夹
* 3.扫描插件
* 4.运行builder函数
* 5.运行插件
* 6.导入java bean(@XImport)
* 7.扫描并加载java bean
* 8.加载渲染关系
* 9.执行bean加完成事件
* a.完成



