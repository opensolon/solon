# solon
一件插件式的微框架

##简单示例一下
```java
XApp app = XApp.start(null);
app.get("/test", cxt -> cxt.output("hello world!"));
```
