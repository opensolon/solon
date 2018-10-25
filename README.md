# solon
一件插件式的微框架

##简单示例一下
```java
XApp app = XApp.start(null);
app.path("/", ()->{
    get("xxx", cxt -> cxt.output("xxxx"));
    get("yyy", cxt -> cxt.output("yyy"));

    path("ddd/**", () -> {
        post("aaa", cxt -> cxt.output("ppp"));
    });
});
```
