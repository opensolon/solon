

### 格式化定制

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