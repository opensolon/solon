package org.noear.solon.scheduling.simple.test.demo1;

import lombok.extern.slf4j.Slf4j;
import org.noear.solon.scheduling.annotation.Scheduled;

import java.util.Date;

/**
 * @author noear 2022/10/15 created
 */
@Slf4j
@Scheduled(fixedRate = 1000 * 1)
public class Job1 implements Runnable {
    @Override
    public void run() {
        log.debug(new Date() + ": 1000 * 1");

        try {
            Thread.sleep(1000 * 2);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
