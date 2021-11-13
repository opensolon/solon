package features;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;
import webapp.dso.EntityConfig;

/**
 * @author noear 2021/9/27 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(webapp.TestApp.class)
public class EntityConfigTest {

    @Inject
    EntityConfig entityConfig;

    @Test
    public void test(){
        assert entityConfig != null;
        assert entityConfig.codes != null;
        assert entityConfig.codes.size() == 2;
        assert entityConfig.likes != null;
        assert entityConfig.likes.size() == 2;
    }
}
