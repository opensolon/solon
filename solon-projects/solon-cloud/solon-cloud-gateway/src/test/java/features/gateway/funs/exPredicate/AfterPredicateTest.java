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
public class AfterPredicateTest {

    @Test
    public void testAfterPredicateValidConfig() {
        // 使用一个较早的时间来确保测试时条件满足
        String validDateTime = ZonedDateTime.now().minusDays(1).toString();
        
        ExPredicate predicate = RouteFactoryManager
                .getPredicate("After", validDateTime);

        assert predicate != null;

        // 测试当前时间（应该在给定时间之后）
        assert predicate.test(new ExContextEmpty());
    }

    @Test
    public void testAfterPredicateInvalidConfig() {
        // 测试无效的时间配置
        assertThrows(DateTimeParseException.class, () -> {
            RouteFactoryManager.getPredicate("After", "invalid-date-time");
        });
    }

    @Test
    public void testAfterPredicateEmptyConfig() {
        // 测试配置为空
        assertThrows(IllegalArgumentException.class, () -> {
            RouteFactoryManager.getPredicate("After", "");
        });
    }

    @Test
    public void testAfterPredicateBoundaryCase() {
        // 使用当前时间作为配置时间边界，测试边界情况
        String boundaryDateTime = ZonedDateTime.now().plusSeconds(1).toString();

        System.out.println(boundaryDateTime);

        ExPredicate predicate = RouteFactoryManager
                .getPredicate("After", boundaryDateTime);

        assert predicate != null;

        // 测试当前时间（当前时间不应该在边界时间之后）
        assert false == predicate.test(new ExContextEmpty());
    }

}
