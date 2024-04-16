package features;

import org.junit.jupiter.api.Test;
import org.noear.solon.cloud.extend.quartz.service.Cron7X;

import java.time.ZoneOffset;

/**
 * @author noear 2024/4/16 created
 */
public class Cron7XTest {
    @Test
    public void cron7x1() {
        Cron7X cron7x = Cron7X.parse("12ms");
        assert cron7x.getInterval() == 12L;
    }

    @Test
    public void cron7x2() {
        Cron7X cron7x = Cron7X.parse("* * * * * ?+08");
        assert cron7x.getZone() == ZoneOffset.ofHours(8);
    }
}
