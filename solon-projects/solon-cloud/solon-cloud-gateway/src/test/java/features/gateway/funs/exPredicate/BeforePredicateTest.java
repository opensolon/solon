package features.gateway.funs.exPredicate;

import features.gateway.funs.ExContextEmpty;
import org.junit.jupiter.api.Test;
import org.noear.solon.cloud.gateway.exchange.ExPredicate;
import org.noear.solon.cloud.gateway.route.RouteFactoryManager;
import org.noear.solon.test.SolonTest;

import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author poppoppuppylove
 * @since 2.9
 */
@SolonTest
public class BeforePredicateTest {

    @Test
    public void testBeforePredicateValidConfig() {
        // 使用一个未来的时间来确保测试时条件满足
        String validDateTime = ZonedDateTime.now().plusDays(1).toString();
        
        ExPredicate predicate = RouteFactoryManager
                .getPredicate("Before", validDateTime);

        assert predicate != null;

        // 测试当前时间（应该在给定时间之前）
        assert predicate.test(new ExContextEmpty());
    }

    @Test
    public void testBeforePredicateInvalidConfig() {
        // 测试无效的时间配置
        assertThrows(DateTimeParseException.class, () -> {
            RouteFactoryManager.getPredicate("Before", "invalid-date-time");
        });
    }

    @Test
    public void testBeforePredicateEmptyConfig() {
        // 测试配置为空
        assertThrows(IllegalArgumentException.class, () -> {
            RouteFactoryManager.getPredicate("Before", "");
        });
    }

    @Test
    public void testBeforePredicateBoundaryCase() {
        // 使用当前时间作为配置时间边界，测试边界情况
        String boundaryDateTime = ZonedDateTime.now().toString();

        System.out.println(boundaryDateTime);

        ExPredicate predicate = RouteFactoryManager
                .getPredicate("Before", boundaryDateTime);

        assert predicate != null;

        // 测试当前时间（当前时间不应该在边界时间之前）
        assert false == predicate.test(new ExContextEmpty());
    }
}
