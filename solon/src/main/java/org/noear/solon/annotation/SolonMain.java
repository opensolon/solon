package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * Solon 主类（入口类）
 *
 * <pre><code>
 * @SolonMain
 * public class App{
 *     public static void main(String[] args){
 *         Solon.start(App.class, args);
 *     }
 * }
 * </code></pre>
 *
 * @author noear
 * @since 2.2
 * */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SolonMain {

}
