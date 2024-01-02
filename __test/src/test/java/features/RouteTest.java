package features;

import org.junit.Test;
import org.noear.solon.auth.impl.AuthRuleImpl;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.route.PathRule;
import org.noear.solon.core.route.RoutingDefault;

/**
 * @author noear 2024/1/2 created
 */
public class RouteTest {
    @Test
    public void routingDefault() {
        //Mvc 里用的 Mapping 路由记录
        RoutingDefault routingDefault = new RoutingDefault("/captchaImage", MethodType.GET, null);
        assert routingDefault.matches(MethodType.GET, "/captchaImage");
        assert routingDefault.matches(MethodType.GET, "/captchaimage") == false;

        routingDefault = new RoutingDefault("/captchaimage", MethodType.GET, null);
        assert routingDefault.matches(MethodType.GET, "/captchaImage") == false;
        assert routingDefault.matches(MethodType.GET, "/captchaimage");
    }

    @Test
    public void authRuleImpl() {
        //AuthRuleImpl 内用的路由规则
        PathRule pathRule = new PathRule();
        pathRule.exclude("/captchaImage");
        assert pathRule.test("/captchaImage") == false;
        assert pathRule.test("/captchaimage");

        pathRule = new PathRule();
        pathRule.exclude("/captchaimage");
        assert pathRule.test("/captchaImage");
        assert pathRule.test("/captchaimage") == false;

        pathRule = new PathRule();
        pathRule.include("/captchaImage");
        assert pathRule.test("/captchaImage");
        assert pathRule.test("/captchaimage") == false;

        pathRule = new PathRule();
        pathRule.include("/captchaimage");
        assert pathRule.test("/captchaImage") == false;
        assert pathRule.test("/captchaimage");
    }
}
