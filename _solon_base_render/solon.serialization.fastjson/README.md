

### 快捷配置（nullXxxAs 会出现多余的null; 但 MapNullValue还不会出现; long 型 null 的没有转成字符串 "0"）

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
     * */
    private static void initMvcJsonCustom(){
        //通过转换器，做简单类型的定制
        FastjsonRenderFactory.global.addConvertor(Date.class,
                (JsonLongConverter<Date>) source -> source.getTime());

        FastjsonRenderFactory.global.addConvertor(LocalDate.class,
                (JsonStringConverter<LocalDate>) source -> source.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        //通过编码器，做复杂类型的原生定制（基于框架原生接口）
        FastjsonRenderFactory.global.addEncoder(Date.class, (ser, obj, o1, type, i) -> {
            SerializeWriter out = ser.getWriter();
            out.writeLong(((Date)obj).getTime());
        });
    }
}
```