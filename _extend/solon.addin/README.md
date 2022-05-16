配置示例

```yaml
solon.addin:
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

* 包名需独立或做为主程序的子包名（避免描扫时扫到别人）
* 插件如果有配置文件，直接在资源里添加文件
  * 用 Solon.cfg().loadAdd("xxx.addin.yml")  ；安排好前缀不要与人冲突；可注入
  * 用 Utils.loadProperties("xxx.addin.yml") 独立使用