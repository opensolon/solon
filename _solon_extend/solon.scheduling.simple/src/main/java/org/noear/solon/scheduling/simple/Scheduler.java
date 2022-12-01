package org.noear.solon.scheduling.simple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author noear
 * @since 1.11
 */
public class Scheduler extends Thread {
    static final Logger log = LoggerFactory.getLogger(Scheduler.class);

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(100);

            } catch (Throwable e) {
                log.error(e.getLocalizedMessage(), e);
            }
        }
    }
}
