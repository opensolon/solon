package io.github.majusko.pulsar2.solon;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用pulsar2配置
 * @author Administrator
 *
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnablePulsar2 {

	/**
     * 消费者聚合开关，会动态执行消费者相关方法
     * */
    boolean consumerAggregator() default false;
	
}
