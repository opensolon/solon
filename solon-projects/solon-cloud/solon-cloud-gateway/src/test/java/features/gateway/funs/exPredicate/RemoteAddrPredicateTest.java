package features.gateway.funs.exPredicate;

import features.gateway.funs.ExContextEmpty;
import org.junit.jupiter.api.Test;
import org.noear.solon.cloud.gateway.exchange.ExPredicate;
import org.noear.solon.cloud.gateway.route.RouteFactoryManager;
import org.noear.solon.test.SolonTest;

/**
 * @author wfm
 * @since 2.9
 */
@SolonTest
public class RemoteAddrPredicateTest {
    @Test
    public void test1() {
        ExPredicate predicate = RouteFactoryManager
                .getPredicate("RemoteAddr", "192.168.1.1");

        assert predicate != null;
        assert predicate.test(buildContext("192.168.1.1"));
        assert !predicate.test(buildContext("192.168.1.2"));
        assert !predicate.test(buildContext("192.168.2.1"));
        assert !predicate.test(buildContext(""));
        assert !predicate.test(buildContext(null));
    }

    @Test
    public void test2() {
        ExPredicate predicate = RouteFactoryManager
                .getPredicate("RemoteAddr", "192.168.1.1/24");

        assert predicate != null;
        assert predicate.test(buildContext("192.168.1.1"));
        assert predicate.test(buildContext("192.168.1.2"));
        assert !predicate.test(buildContext("192.168.2.1"));
    }

    @Test
    public void test3() {
        ExPredicate predicate = RouteFactoryManager
                .getPredicate("RemoteAddr", "2001:0db8:85a3:0000:0000:8a2e:0370:7334");

        assert predicate != null;
        assert predicate.test(buildContext("2001:0db8:85a3:0000:0000:8a2e:0370:7334"));
        assert !predicate.test(buildContext("2001:0db8:85a3:0000:0000:8a2e:0370:7335"));
        assert !predicate.test(buildContext("2001:0db8:85a3:0000:0000:8a2e:0371:7334"));
    }

    @Test
    public void test4() {
        ExPredicate predicate = RouteFactoryManager
                .getPredicate("RemoteAddr", "2001:0db8:85a3:0000:0000:8a2e:0370:7334/112");

        assert predicate != null;
        assert predicate.test(buildContext("2001:0db8:85a3:0000:0000:8a2e:0370:7334"));
        assert predicate.test(buildContext("2001:0db8:85a3:0000:0000:8a2e:0370:7335"));
        assert !predicate.test(buildContext("2001:0db8:85a3:0000:0000:8a2e:0371:7334"));
    }

    private ExContextEmpty buildContext(String ip){
        return new ExContextEmpty() {
            @Override
            public String realIp() {
                return ip;
            }
        };
    }
}
