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
package org.noear.solon.cloud.model;

import org.noear.solon.Utils;
import org.noear.solon.data.tran.TranListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 事件事务
 *
 * @author noear
 * @since 2.8
 */
public class EventTran implements TranListener {
    private static Logger log = LoggerFactory.getLogger(EventTran.class);

    private Map<Class<?>, EventTranListener> listeners = new LinkedHashMap<>();
    private String id;

    /**
     * 获取事务Id
     */
    public String getId() {
        if (id == null) {
            id = Utils.guid();
        }

        return id;
    }

    /**
     * 设置监听器
     */
    public void setListener(EventTranListener listener) {
        listeners.put(listener.getClass(), listener);
    }

    /**
     * 获取监听器
     */
    public <T extends EventTranListener> T getListener(Class<T> tClass) {
        return (T) listeners.get(tClass);
    }

    /**
     * 事务提交
     */
    public void commit() throws Exception {
        for (Map.Entry<Class<?>, EventTranListener> entry : listeners.entrySet()) {
            entry.getValue().onCommit();
        }
    }

    /**
     * 事务回滚
     */
    public void rollback() {
        for (Map.Entry<Class<?>, EventTranListener> entry : listeners.entrySet()) {
            try {
                entry.getValue().onRollback();
            } catch (Throwable e) {
                log.warn("Rollback failure", e);
            }
        }
    }

    /////////////////////////
    // 支持 jdbc 事务对接
    ////////////////////////

    @Override
    public void beforeCommit(boolean readOnly) throws Throwable {
        this.commit();
    }

    @Override
    public void afterCompletion(int status) {
        if (status == TranListener.STATUS_ROLLED_BACK) {
            this.rollback();
        }
    }
}