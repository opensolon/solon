package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * 配置器（主要为了动态构建组件 或 适配些接口）
 *
 * <pre><code>
 * //或构建一些组件
 * @Configuration
 * public class Config{
 *     @Bean
 *     public DataSource db1(@Inject("${db1}") HikariDataSource ds){
 *         return ds;
 *     }
 * }
 *
 * //或完成一些适配
 * @Configuration
 * public class ServletConfig implements ServletContainerInitializer {
 *     @Override
 *     public void onStartup(Set<Class<?>> set, ServletContext sc) throws ServletException {
 *
 *     }
 * }
 * </code></pre>
 *
 * @author noear
 * @since 1.0
 * */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Configuration {

}
