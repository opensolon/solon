package webapp.demo0_bean;

import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.data.annotation.Tran;

/**
 * @author noear 2023/10/14 created
 */
@Component
public class AopDemoCom2 {
    @Inject
    AopDemoCom1 demoCom1;

    @Inject
    AopDemoCom2 self;
    @Tran
    public void test(){
        self.test1();
        self.test2();
    }

    public void test1(){
        demoCom1.hello();
    }

    protected void test2(){
        demoCom1.hello();
    }
}
