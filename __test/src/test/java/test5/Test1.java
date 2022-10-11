package test5;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.Utils;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonJUnit4ClassRunner;

/**
 * @author noear 2022/10/9 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
public class Test1 {

    @Inject(value = "${cfgtestddd2}",required = true)
    String cfgTest;

    @Test
    public void test(){
        System.out.println("hello");
    }

//    @Test
//    public void test() throws Exception{
//        Model1 model1 = Model1.class.newInstance();
//        assert model1 != null;
//    }
//
//    @Test
//    public void test2() throws Exception{
//        Model1 model1 = Utils.newInstance(Model1.class);
//        assert model1 != null;
//    }
}
