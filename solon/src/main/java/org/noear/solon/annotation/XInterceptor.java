package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * 拦截器（争对路径拦截，以控制器的模式写拦截器）
 *
 * <pre><code>
 * @XInterceptor
 * public class DemoInterceptor{
 *
 * }
 *
 * </code></pre>
 * @author noear
 * @since 1.0
 * */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XInterceptor {
}

