package org.noear.solon.scheduling.simple.test.features;

import lombok.extern.slf4j.Slf4j;
import org.noear.solon.scheduling.annotation.Scheduled;

import java.util.Date;

/**
 * @author noear 2021/12/28 created
 */
@Slf4j
@Scheduled(fixedRate = 1000 * 3)
public class Job11 implements Runnable {
    @Override
    public void run() {
        log.debug(new Date() + ": 1000*3");
    }
}
