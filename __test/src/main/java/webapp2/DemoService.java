package webapp2;

import org.noear.solon.annotation.Inject;
import org.noear.solon.aspect.annotation.Service;
import org.noear.solon.data.annotation.Tran;

/**
 * @author noear 2022/8/30 created
 */
@Service
public class DemoService {
    @Inject
    DemoService my;

    @Tran
    public void add1(){
        //注入的 my 是代理类 //只有调用代理类的函数，才能触发拦截能力
        my.add2();
    }

    @Tran
    public void add2(){

    }
}
