package org.noear.solon.annotation;

import org.noear.solon.core.handle.Handler;
import java.lang.annotation.*;


/**
 * 触发器：后置处理（争对 Controller 或 Action 的拦截器）
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
 * </code></pre>
 *
 * @author noear
 * @since 1.0
 * */
@Inherited //要可继承
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface After {
    Class<? extends Handler>[] value();
}
