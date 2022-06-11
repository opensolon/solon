package features;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;
import webapp.models.CfgItem;

import java.util.List;

/**
 * @author noear 2022/6/11 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(webapp.TestApp.class)
public class PropTest {
    @Inject("${cfgitems}")
    List<CfgItem> cfgitems;

    @Test
    public void test(){
        System.out.println(cfgitems);

        assert cfgitems != null;
        assert cfgitems.size() == 2;
        assert cfgitems.get(0).getId() == 1;
    }
}
