package features.gateway.funs.exPredicate;

import features.gateway.funs.ExContextEmpty;
import org.junit.jupiter.api.Test;
import org.noear.solon.cloud.gateway.exchange.ExPredicate;
import org.noear.solon.cloud.gateway.route.RouteFactoryManager;
import org.noear.solon.test.SolonTest;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author noear
 * @since 2.9
 */
@SolonTest
public class HostPredicateTest {
    @Test
    public void testValidConfig() {
        ExPredicate predicate = RouteFactoryManager
                .getPredicate("Host", "*.demo.com");

        assert predicate != null;
        assert predicate.test(new ExContextEmpty() {
            @Override
            public URI rawURI() {
                return URI.create("https://a.demo.com");
            }
        });
        assert predicate.test(new ExContextEmpty() {
            @Override
            public URI rawURI() {
                return URI.create("https://b.demo.com");
            }
        });
        assert false == predicate.test(new ExContextEmpty() {
            @Override
            public URI rawURI() {
                return URI.create("https://c.test.com");
            }
        });
    }

    @Test
    public void testValidConfig2() {
        ExPredicate predicate = RouteFactoryManager
                .getPredicate("Host", "a*.demo.com");

        assert predicate != null;
        assert predicate.test(new ExContextEmpty() {
            @Override
            public URI rawURI() {
                return URI.create("https://aa.demo.com");
            }
        });
        assert predicate.test(new ExContextEmpty() {
            @Override
            public URI rawURI() {
                return URI.create("https://ab.demo.com");
            }
        });
        assert false == predicate.test(new ExContextEmpty() {
            @Override
            public URI rawURI() {
                return URI.create("https://bb.demo.com");
            }
        });
        assert false == predicate.test(new ExContextEmpty() {
            @Override
            public URI rawURI() {
                return URI.create("https://c.test.com");
            }
        });
    }

    @Test
    public void testEmptyConfig() {
        assertThrows(IllegalArgumentException.class, () -> {
            RouteFactoryManager.getPredicate("Host", "");
        });
    }
}
