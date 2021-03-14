package org.noear.solon.cloud.impl;

import org.noear.solon.cloud.model.Entry;

import java.util.concurrent.Semaphore;

/**
 * 本地中断器入口
 *
 * @author noear
 * @since 1.3
 */
public class CloudEntryLocalImp implements Entry {
    private Semaphore semaphore;

    public CloudEntryLocalImp(int total) {
        semaphore = new Semaphore(total);
    }

    @Override
    public void enter(int count) throws InterruptedException {
        semaphore.acquire(count);
    }

    @Override
    public void exit(int count) {
        semaphore.release(count);
    }
}
