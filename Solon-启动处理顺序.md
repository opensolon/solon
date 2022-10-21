## Solon 启动处理顺序

* 1.实例化 Solon.app() 
* 2.初始化配置
* 3.加载扩展文件夹
* 4.扫描插件并排序
* 5.运行 initialize 函数
* 6.推送 AppInitEndEvent [事件]
* 7.运行插件
* 8.推送 PluginLoadEndEvent [事件]
* 9.导入java bean(@Import)
* a.扫描并加载java bean
* b.推送 BeanLoadEndEvent [事件]
* c.加载渲染印映关系
* d.执行 Aop.beanLoaded 函数
* e.推送 AppLoadEndEvent [事件]
* f.结束
