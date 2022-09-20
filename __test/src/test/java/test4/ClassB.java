package test4;

import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

/**
 * @author noear 2022/9/20 created
 */
@Component
public class ClassB {
    @Inject
    ClassC classC;
}
