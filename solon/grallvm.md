1、支持通过预配置的方式指定scan文件清单(for grallvm native image)
2、添加scan日志，方便提取scan清单
3、文件清单配置方式：
app.properties->solon.scan
4、例子：
```properties
solon.scan=META-INF/solon/solon.boot.jlhttp.properties,META-INF/solon/solon.data.properties,com/hx/config/Config.class
```
```properties
solon.scan[0]=META-INF/solon/solon.boot.jlhttp.properties
solon.scan[1]=META-INF/solon/solon.data.properties
solon.scan[2]=com/hx/config/Config.class
```
```yaml
solon.scan:
  - META-INF/solon/solon.boot.jlhttp.properties
  - META-INF/solon/solon.data.properties
  - com/hx/config/Config.class
```
5、文件清单提取方式： 
本地运行，找到所有关键字：[Solon] Scan completed: [... , ... ,...]把[]里的内容抓取出来合并到一起即可