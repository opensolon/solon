package test5;

import org.junit.jupiter.api.Test;
import org.noear.solon.Utils;

/**
 * @author noear 2022/10/9 created
 */
public class Test1 {
    @Test
    public void test() throws Exception{
        Model1 model1 = Model1.class.newInstance();
        assert model1 != null;
    }

    @Test
    public void test2() throws Exception{
        Model1 model1 = Utils.newInstance(Model1.class);
        assert model1 != null;
    }
}
