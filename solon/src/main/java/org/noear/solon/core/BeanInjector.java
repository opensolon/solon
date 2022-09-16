package org.noear.solon.core;

import java.lang.annotation.Annotation;

/**
 * Bean 注入器（此类用于扩展AopContext，为其添加注入器）
 *
 * <pre><code>
 * //@Db 注入器添加
 * Solon.context().beanInjectorAdd(Db.classs, (varH, anno)->{
 *     ...
 * });
 *
 * //@Db demo
 * @Component
 * public class DemoBean{
 *     @Db("db1")
 *     UserMapper userMapper;
 * }
 * </code></pre>
 *
 * @author noear
 * @since 1.0
 * */
@FunctionalInterface
public interface BeanInjector<T extends Annotation> {
    /**
     * 注入
     * */
    void doInject(VarHolder varH, T anno);
}
