
```yaml
solon.addin:
  add1: "/x/x/x.jar"
  add2: "/x/x/x2.jar"
```

```java

public class DemoApp {
    public static void main(String[] args) {
        AddinLoader.add("add2", "/x/x/x2.jar");
        AddinLoader.remove("add2");
        
        AddinLoader.load("add2");
        AddinLoader.start("add2");
        AddinLoader.stop("add2");
        AddinLoader.unload("add2");
        
        AddinLoader.loadJar(file);
        AddinLoader.unloadJar(packge);
    }
}
```