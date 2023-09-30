package features;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;
import webapp.App;
import webapp.demo7_test.DynamicService;

/**
 * @author noear 2023/9/1 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(App.class)
public class DataTest {

    @Inject
    DynamicService dynamicService;

    @Test
    public void test1() throws Exception{
        assert  "db_rock1".equals(dynamicService.test1());
        assert  "db_rock2".equals(dynamicService.test2());
        assert  "".equals(dynamicService.test3());
    }
}
