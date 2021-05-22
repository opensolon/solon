package webapp.demo6_aop;

import org.noear.solon.annotation.Mapping;

import java.lang.annotation.*;

/**
 * @author noear 2021/5/22 created
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExTest {
}
