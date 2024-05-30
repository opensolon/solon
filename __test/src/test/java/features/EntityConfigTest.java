package features;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonJUnit5Extension;
import org.noear.solon.test.SolonTest;
import webapp.App;
import webapp.dso.EntityConfig;

/**
 * @author noear 2021/9/27 created
 */
@SolonTest(App.class)
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
