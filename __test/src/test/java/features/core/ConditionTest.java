package features.core;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.snack.ONode;
import org.noear.snack.core.Feature;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;
import webapp.dso.ConditionConfig;

/**
 * @author noear 2023/2/5 created
 */
@Configuration
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(webapp.TestApp.class)
public class ConditionTest {

    @Inject
    ConditionConfig config;

    @Test
    public void hasPropertyTest(){
        assert config.getUsername() != null;
        assert config.getUsername2() == null;
        assert "noear".equals(config.getUsername3());
        assert config.getUsername4() == null;
        assert "noear".equals(config.getUsername5());

        System.out.println(ONode.load(config, Feature.SerializeNulls ));
    }
}
