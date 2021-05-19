## Solon 启动处理顺序

* 1.实例化 Solon.global() 并加载配置
* 2.加载扩展文件夹
* 3.扫描插件并排序
* 4.运行 initialize 函数
* 5.推送 AppInitEndEvent [事件]
* 6.运行插件
* 7.推送 PluginLoadEndEvent [事件]
* 8.导入java bean(@Import)
* 9.扫描并加载java bean
* a.推送 BeanLoadEndEvent [事件]
* b.加载渲染印映关系
* c.执行bean加完成事件
* d.推送 AppLoadEndEvent [事件]
* e.结束


