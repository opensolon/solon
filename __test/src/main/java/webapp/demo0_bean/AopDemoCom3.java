package webapp.demo0_bean;

import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Destroy;
import org.noear.solon.annotation.Init;

/**
 * @author fzdwx
 */
@Component
public class AopDemoCom3 {

    @Init
    public void myInit() {
        System.out.println("call init");
    }

    @Destroy
    public void myDestroy() {
        System.out.println("call destroy");
    }
}
