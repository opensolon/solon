package org.noear.nami.annotation;

import org.noear.nami.NamiConfiguration;
import org.noear.nami.NamiConfigurationDefault;

import java.lang.annotation.*;

/**
 * Nami 客户端
 *
 * @author noear
 * @since 1.0
 * */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NamiClient {

    /**
     * 完整的url地址（url）
     * */
    String url() default "";

    /**
     * 服务组
     * */
    String group() default "";

    /**
     * 服务名
     * */
    String name() default "";

    /**
     * 路径
     * */
    String path() default "";

    /**
     * 添加头信息
     *
     * 例：{"xxx:xxx","yyy:yyy"}
     * */
    String[] headers() default {};

    /**
     * 负载（用于方便演示，设定固定负载）
     * */
    String[] upstream() default {};

    /**
     * 超时（单为：秒）
     * */
    int timeout() default 0;

    /**
     * 心跳（单为：秒）
     * */
    int heartbeat() default 0;

    /**
     * 指定配置器
     * */
    Class<? extends NamiConfiguration> configuration() default NamiConfigurationDefault.class;
}

