package initTest.test3;

import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

/**
 * @author noear 2022/9/20 created
 */
@Component
public class ClassC {
    @Inject
    ClassB classB;

    @Inject
    ClassD classD;
}
