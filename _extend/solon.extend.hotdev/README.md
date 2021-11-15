
## 使用说明

* 1、开发时，使用 Hotdev.start() 替代 Solon.start()。编译代码后会自动重启
* 2、开发工具需要设置自动编译，否则需要手动编译更改过的类文件


### 示例：

```java
public class DemoApp {
    public static void main(String[] args) {
        Hotdev.start(DemoApp.class, args);
    }
}
```