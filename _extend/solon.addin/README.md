
```yaml
solon.addin:
  add1: "/x/x/x.jar"
  add2: "/x/x/x2.jar"
```

```java

public class DemoApp {
    public static void main(String[] args) {
        AddinManager.add("add2", "/x/x/x2.jar");
        AddinManager.remove("add2");

        AddinManager.load("add2");
        AddinManager.start("add2");
        AddinManager.stop("add2");
        AddinManager.unload("add2");

        AddinManager.loadJar(file);
        AddinManager.unloadJar(packge);
    }
}
```