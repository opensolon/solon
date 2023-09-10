package org.noear.dami.solon.annotation;

import java.lang.annotation.*;

/**
 * 大米组件
 *
 * @author noear
 * @since 2.5
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Dami {
    /**
     * 主题映射
     * */
    String topicMapping();
}
