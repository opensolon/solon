package labs.importTest.case2;

import org.noear.solon.annotation.Import;

import java.lang.annotation.*;

/**
 * @author noear 2023/10/18 created
 */
@Import(classes = DemoConfig.class, propertySource = "classpath:demo.properties")
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableDemo {
}
