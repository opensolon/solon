package org.noear.solon.annotation;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 初始化（相当于 PostConstruct，也不同。可以在任何 Bean 里使用）
 *
 * Bean 构建过程：Constructor(构造方法) -> @Inject(依赖注入) -> @Init(初始化)
 *
 * <pre><code>
 * @Component
 * public class DemoBean{
 *     @Inject("${db1}")
 *     Properties props;
 *
 *     @Inject("${user.name}")
 *     String name;
 *
 *     public DemoBean(){
 *         //此时 props,name === null
 *     }
 *
 *     @Init
 *     public void init(){
 *         //此时 props,name !== null
 *     }
 * }
 * </code></pre>
 *
 * @author noear
 * @since 1.0
 * */
@Retention (RUNTIME)
@Target(METHOD)
@Documented
public @interface Init {
    /**
     * 延时执行
     * */
    boolean delay() default true;
    /**
     * 优化级
     * */
    int priority() default 0;
}
