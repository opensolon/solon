package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * 单例
 *
 * 一般附加在XController上；可继承；（所有Bean默认都是单例）
 *
 * <pre><code>
 * @XSingleton(false)
 * @XController
 * public class DemoController{
 *
 * }
 * </code></pre>
 *
 * @author noear
 * @since 1.0
 * */
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface XSingleton {
    boolean value();
}