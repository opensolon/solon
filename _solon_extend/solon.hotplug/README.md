配置示例

```yaml
solon.hotplug:
  add1: "/x/x/x.jar"
  add2: "/x/x/x2.jar"
```

接口示例

```java
public class DemoApp {
    public static void main(String[] args) {
        //管理插件
        PluginManager.add("add2", "/x/x/x2.jar");
        PluginManager.remove("add2");

        PluginManager.load("add2");
        PluginManager.start("add2");
        PluginManager.stop("add2");
        PluginManager.unload("add2");

        //直接操控文件
        PluginManager.loadJar(file);
        PluginManager.unloadJar(packge);
    }
}
```

注意事项：

* 插件包名需独立性（避免描扫时扫到别人）
  * 例主程为：xxx
  * 插件1为：xxx.add1
  * 插件2为：xxx.add2
