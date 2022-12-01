package org.noear.solon.scheduling.simple.test.demo1;

import lombok.extern.slf4j.Slf4j;
import org.noear.solon.scheduling.annotation.Scheduled;

import java.util.Date;

/**
 * @author noear 2022/11/24 created
 */
@Slf4j
@Scheduled(cron = "1s")
public class Job13 implements Runnable {
    @Override
    public void run() {
        log.info(new Date() + ": 1s");
    }
}
