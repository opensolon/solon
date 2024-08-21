package features.exPredicate;

import features.ExContextEmpty;
import org.junit.jupiter.api.Test;
import org.noear.solon.cloud.gateway.exchange.ExPredicate;
import org.noear.solon.cloud.gateway.route.RouteFactoryManager;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author noear
 * @since 2.9
 */
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
    public void testEmptyConfig() {
        assertThrows(IllegalArgumentException.class, () -> {
            RouteFactoryManager.getPredicate("Host", "");
        });
    }
}
