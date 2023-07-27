package micrometer.annotation;

import java.lang.annotation.*;

/**
 * 计计数器
 * 统计次数
 *
 * @author bai
 * @date 2023/07/21
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MeterCounter {

    /**
     * 值
     *
     * @return {@link String}
     */
    String value();


    /**
     * 类型
     *
     * @return {@link String}
     */
    String type() default "counter";

    /**
     * 标签
     *
     * @return {@link String[]}
     */
    String[] tags() default {};

    /**
     * 启用
     *
     * @return boolean
     */
    boolean enable() default true;
}
