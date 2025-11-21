package features.scheduling.simple;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.util.TimeZone;

/**
 *
 * @author noear 2025/11/14 created
 *
 */
public class ZoneTest {
    @Test
    public void case11() {
        String c1 = "Asia/Shanghai";
        String c2 = "GMT+08:00"; //不能用：+08

        TimeZone t1 = TimeZone.getTimeZone(c1);
        TimeZone t2 = TimeZone.getTimeZone(c2);

        assert t1.getRawOffset() == t2.getRawOffset();
    }

    @Test
    public void case12() {
        String c1 = "Asia/Shanghai";
        String c2 = "+08"; //不能用：+08

        TimeZone t1 = TimeZone.getTimeZone(c1);
        TimeZone t2 = TimeZone.getTimeZone(c2);

        assert t1.getRawOffset() != t2.getRawOffset();
        assert TimeZone.getTimeZone("GMT").getRawOffset() == t2.getRawOffset();
    }

    @Test
    public void case21() {
        String c1 = "Asia/Shanghai";
        String c2 = "GMT+08:00"; //不能用：+08

        ZoneId t1 = ZoneId.of(c1);
        ZoneId t2 = ZoneId.of(c2);

        System.out.println(t1.getRules().getStandardOffset(Instant.now()));
        assert t1.getRules().getStandardOffset(Instant.now()) == t2.getRules().getStandardOffset(Instant.now());
    }

    @Test
    public void case22() {
        String c1 = "Asia/Shanghai";
        String c2 = "+08"; //不能用：+08

        ZoneId t1 = ZoneId.of(c1);
        ZoneId t2 = ZoneId.of(c2);

        System.out.println(t1.getRules().getStandardOffset(Instant.now()));
        assert t1.getRules().getStandardOffset(Instant.now()) == t2.getRules().getStandardOffset(Instant.now());
    }
}
