package org.noear.solon.scheduling.simple.test.demo5;


import lombok.extern.slf4j.Slf4j;
import org.noear.solon.scheduling.annotation.Scheduled;

import java.util.Date;

/**
 * @author noear 2022/6/26 created
 */
@Slf4j
@Scheduled(cron = "* * * * * ? *", concurrent = true)
public class Job1 implements Runnable {
    @Override
    public void run() {
        log.info("{}", new Date());
    }
}
