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
        AddinManager.add("add2", "/x/x/x2.jar");
        AddinManager.remove("add2");

        AddinManager.load("add2");
        AddinManager.start("add2");
        AddinManager.stop("add2");
        AddinManager.unload("add2");

        //直接操控文件
        AddinManager.loadJar(file);
        AddinManager.unloadJar(packge);
    }
}
```

注意事项：

* 插件包名需独立性（避免描扫时扫到别人）
  * 例主程为：xxx
  * 插件1为：xxx.add1
  * 插件2为：xxx.add2
* 插件的配置支持隔离模式与非隔离模式

  ```java
  public class DemoPlugin1 extends PluginPlus{
      //配置不隔离
  }
  
  public class DemoPlugin2 extends PluginPlus{
      public DemoPlugin2(){
          super(new Props());  //配置隔离
      }
  }
  ```