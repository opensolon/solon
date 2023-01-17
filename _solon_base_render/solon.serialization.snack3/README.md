

### 快捷配置（完美支持）

```yaml
solon.serialization.json:
  dateAsFormat: 'yyyy-MM-dd HH:mm:ss' #配置日期格式（默认输出为时间戳）
  dateAsTimeZone: 'GMT+8' #配置时区
  dateAsTicks: false #将date转为毫秒数（和 dateAsFormat 二选一）
  longAsString: true #将long型转为字符串输出 （默认为false）
  boolAsInt: false   #将bool型转为字符串输出 （默认为false）
  nullStringAsEmpty: false
  nullBoolAsFalse: false
  nullNumberAsZero: false
  nullArrayAsEmpty: false
  nullAsWriteable: false #输出所有null值
```


### 高级格式化定制

```java
public class DemoApp {
    public static void main(String[] args){
        Solon.start(DemoApp.class, args, app->{
            initMvcJsonCustom();
        });
    }

    /**
     * 初始化json定制（需要在插件运行前定制）
     */
    private static void initMvcJsonCustom() {
        //通过转换器，做简单类型的定制
        SnackRenderFactory.global
                .addConvertor(Date.class, s -> s.getTime());

        SnackRenderFactory.global
                .addConvertor(LocalDate.class, s -> s.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        SnackRenderFactory.global
                .addConvertor(LocalDateTime.class, s -> s.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));


        SnackRenderFactory.global.add
    }
}
```