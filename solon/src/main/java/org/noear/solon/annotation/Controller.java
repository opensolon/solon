package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * mvc::Web 组件（控制器，一般与@Mapping 配合使用）
 *
 * <pre><code>
 * @Valid        //增加验证支持
 * @Controller
 * public class DemoController{
 *     @NotNull({"name","message"})
 *     @Mapping("/hello/")
 *     public String hello(String name, String message){
 *         return "Hello " + name;
 *     }
 *
 *     @Mapping("/cmd/{cmd}")
 *     public String cmd(@NotNull String cmd){
 *         return "cmd = " + cmd;
 *     }
 * }
 * </code></pre>
 *
 * @author noear
 * @since 1.0
 * */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Controller {
}
