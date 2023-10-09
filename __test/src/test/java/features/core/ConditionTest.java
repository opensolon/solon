package features.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.snack.ONode;
import org.noear.snack.core.Feature;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonJUnit5Extension;
import org.noear.solon.test.SolonTest;
import webapp.App;
import webapp.dso.ConditionConfig;
import webapp.models.BaseRequest;

/**
 * @author noear 2023/2/5 created
 */
@Configuration
@ExtendWith(SolonJUnit5Extension.class)
@SolonTest(App.class)
public class ConditionTest {

    @Inject
    ConditionConfig config;

    @Inject
    BaseRequest baseRequest;

    @Test
    public void test(){
        assert config.getUsername() != null;
        assert config.getUsername2() == null;
        assert "noear".equals(config.getUsername3());
        assert config.getUsername4() == null;
        assert "noear".equals(config.getUsername5());

        assert "noear".equals(config.getUsername11());
        assert config.getUsername12() == null;

        assert "noear".equals(config.getUsername21());
        assert config.getUsername22() == null;

        System.out.println(ONode.load(config, Feature.SerializeNulls ));

        assert baseRequest != null;
        System.out.println(ONode.stringify(baseRequest));
    }
}
