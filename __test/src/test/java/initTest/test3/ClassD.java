package initTest.test3;

import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Init;
import org.noear.solon.annotation.Inject;

/**
 * @author noear 2022/9/21 created
 */
@Component
public class ClassD {

    @Init
    public void init(){
        System.out.println("ClassD");
    }
}
