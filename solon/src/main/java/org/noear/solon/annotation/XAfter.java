package org.noear.solon.annotation;

import org.noear.solon.core.XHandler;
import java.lang.annotation.*;


/**
 * 触发器：后置处理（争对 XController 和 XAction 的拦截器）
 *
 * <pre><code>
 * @XBefore({StartHandler.class, IpHandler.class})
 * @XAfter(EndHandler.class)
 * @XController
 * public class DemoController{
 *     @XMapping("/demo/*")
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
public @interface XAfter {
    Class<? extends XHandler>[] value();
}
