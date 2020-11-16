package org.noear.solon.annotation;

import org.noear.solon.core.handler.Handler;
import java.lang.annotation.*;

/**
 * 触发器：前置处理（争对 XController 和 XAction 的拦截器）
 *
 * <pre><code>
 * @Before({StartHandler.class, IpHandler.class})
 * @After(EndHandler.class)
 * @Controller
 * public class DemoController{
 *     @Mapping("/demo/*")
 *     public String hello(){
 *         return "heollo world;";
 *     }
 * }
 *
 * //
 * // 注解传导示例：（用于简化使用）
 * //
 * @Before({ValidateInterceptor.class})
 * @Inherited
 * @Target({ElementType.TYPE, ElementType.METHOD})
 * @Retention(RetentionPolicy.RUNTIME)
 * public @interface XValid {
 *     ...
 * }
 * </code></pre>
 *
 * @author noear
 * @since 1.0
 * */
@Inherited //要可继承
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Before {
    Class<? extends Handler>[] value();
}
