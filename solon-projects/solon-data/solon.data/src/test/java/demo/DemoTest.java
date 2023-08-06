package demo;

import org.noear.solon.data.annotation.Tran;
import org.noear.solon.data.annotation.TranAnno;
import org.noear.solon.data.tran.TranUtils;

/**
 * @author noear 2022/6/30 created
 */
public class DemoTest {

    //两者效果相同

    public void test1() throws Throwable{
        TranUtils.execute(new TranAnno().readOnly(true),()->{
            //..
        });
    }

    @Tran(readOnly = true)
    public void test2(){
        //..
    }
}
