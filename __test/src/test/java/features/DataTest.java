package features;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonJUnit5Extension;
import org.noear.solon.test.SolonTest;
import webapp.App;
import webapp.demo7_test.DynamicService;

/**
 * @author noear 2023/9/1 created
 */
@ExtendWith(SolonJUnit5Extension.class)
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
