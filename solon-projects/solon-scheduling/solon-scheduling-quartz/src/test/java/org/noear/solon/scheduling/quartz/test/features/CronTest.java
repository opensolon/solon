package org.noear.solon.scheduling.quartz.test.features;

import org.junit.jupiter.api.Test;
import org.quartz.CronExpression;

/**
 * @author noear 2025/6/9 created
 */
public class CronTest {
    @Test
    public void case1() throws Exception {
        new CronExpression("0 0 10-20 * * ?");
    }
}
