package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * 导入器，通过注解导入类或者包（只在app 或 config 有效）
 *
 * <pre><code>
 * //注解传导示例
 * @Retention(RetentionPolicy.RUNTIME)
 * @Target(ElementType.TYPE)
 * @XImport(value = DubboConfiguration.class)
 * public @interface EnableDubbo {
 *     ...
 * }
 *
 * //注解在应用上
 * @XImport(value = DemoConfiguration.class)
 * @EnableDubbo
 * public class DemoApp{
 *     public static void main(String[] args){
 *         XApp.start(DemoApp.class, args);
 *     }
 * }
 *
 * //::XImport 注解在应用上的执行顺位：XPlugin -> XImport -> Scan bean
 * </code></pre>
 *
 * @author noear
 * @since 1.0
 * */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XImport {
    Class<?>[] value() default {};
    String[] scanPackages() default {};
}
