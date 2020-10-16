package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * mvc::Web 组件（控制器，一般与@XMapping 配合使用）
 *
 * <pre><code>
 * @XValid        //增加验证支持
 * @XController
 * public class DemoController{
 *     @XMapping("/hello/")
 *     public String hello(@NotNull String name){
 *         return "Hello " + name;
 *     }
 *
 *     @XMapping("/cmd/{cmd}")
 *     public String cmd(String cmd){
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
public @interface XController {
}
