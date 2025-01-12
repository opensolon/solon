/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.logging.persistent;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 打包队列（一个个添加；打包后批量处理）
 * 提交时，每次提交100条；消费完后暂停0.1秒
 *
 * @author noear
 * @since 2.2
 * */
public class PackagingQueueTaskImpl<Event> implements PackagingQueueTask<Event>, Closeable {
    /**
     * 休息时间
     * */
    private long idleInterval = 500; //必须大于等于min
    private final long idleInterval_min = 10;

    /**
     * 包装合大小
     * */
    private int packetSize = 150; //必须大于等于150
    private final int packetSize_min = 150;

    private Thread workThread;

    private PackagingWorkHandler<Event> workHandler;

    public PackagingQueueTaskImpl() {
        workThread = new Thread(() -> {
            workStartDo();
        }, "Simple task");

        workThread.start();
    }



    public void setWorkHandler(PackagingWorkHandler<Event> workHandler) {
        this.workHandler = workHandler;
    }


    //
    //
    //

    private Queue<Event> queueLocal = new LinkedBlockingQueue<>();

    public void add(Event event) {
        try {
            queueLocal.add(event);
        } catch (Exception ex) {
            //不打印，不推出
            ex.printStackTrace();
        }
    }

    public void addAll(Collection<Event> events) {
        try {
            queueLocal.addAll(events);
        } catch (Exception ex) {
            //不打印，不推出
            ex.printStackTrace();
        }
    }

    /**
     * 空闲休息时间
     * */
    public long getInterval() {
        return idleInterval;
    }

    /**
     * 设置空闲休息时间
     * */
    public void setIdleInterval(long interval) {
        if (interval >= idleInterval_min) {
            this.idleInterval = interval;
        }
    }

    /**
     * 设置包装合大小
     * */
    public void setPacketSize(int packetSize) {
        if (packetSize >= packetSize_min) {
            this.packetSize = packetSize;
        }
    }

    //
    // 打包处理控制
    //

    private void workStartDo() {
        while (true) {
            if(isStopped){
                return;
            }

            try {
                long time_start = System.currentTimeMillis();
                this.workExecDo();
                long time_end = System.currentTimeMillis();

                if (this.getInterval() == 0) {
                    return;
                }

                if ((time_end - time_start) < this.getInterval()) {
                    Thread.sleep(this.getInterval());
                }

            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }
    }

    private void workExecDo() throws Exception {
        if (workHandler == null) {
            return;
        }

        while (true) {
            if(isStopped){
                return;
            }

            List<Event> list = new ArrayList<>(packetSize);

            collectDo(list);

            if (list.size() > 0) {
                workHandler.onEvents(list);
            } else {
                break;
            }
        }
    }

    private void collectDo(List<Event> list) {
        //收集最多100条日志
        //
        int count = 0;
        while (true) {
            if(isStopped){
                return;
            }

            Event event = queueLocal.poll();

            if (event == null) {
                break;
            } else {
                list.add(event);
                count++;

                if (count == packetSize) {
                    break;
                }
            }
        }
    }

    private  boolean isStopped = false;

    @Override
    public void close() throws IOException {
        isStopped = true;
    }
}
