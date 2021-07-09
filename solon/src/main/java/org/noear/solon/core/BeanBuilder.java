package org.noear.solon.core;

import java.lang.annotation.Annotation;

/**
 * Bean 构建器（此类用于扩展AopContext，为其添加构建器）
 *
 * <pre><code>
 * //@Cron4j 构建器添加
 * Aop.context().beanBuilderAdd(Cron4j.classs, (clz, bw, anno)->{
 *     ...
 * });
 *
 * //@Cron4j demo
 * @Cron4j(cronx = "*\/1 * * * *")
 * public class DemoJob implements Task{
 *     @Db("db1")
 *     UserMapper userMapper;
 *
 *     public void execute(TaskExecutionContext context) throws RuntimeException{
 *         ...
 *     }
 * }
 * </code></pre>
 *
 * @author noear
 * @since 1.0
 * */
@FunctionalInterface
public interface BeanBuilder<T extends Annotation> {
    /**
     * 构建
     *
     * @param clz 类
     * @param anno 注解
     * */
    void doBuild(Class<?> clz, BeanWrap bw, T anno) throws Exception;
}
