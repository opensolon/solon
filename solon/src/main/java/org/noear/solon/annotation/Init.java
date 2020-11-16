package org.noear.solon.annotation;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 初始化（相当于 PostConstruct）
 *
 * Bean 构建过程：Constructor(构造方法) -> @XInject(依赖注入) -> @XInit(初始化)
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

}
