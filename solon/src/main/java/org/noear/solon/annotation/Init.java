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
 * @XBean
 * public class DemoBean{
 *     @XInject("${db1}")
 *     Properties props;
 *
 *     @XInject("${user.name}")
 *     String name;
 *
 *     public DemoBean(){
 *         //此时 props,name === null
 *     }
 *
 *     @XInit
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
