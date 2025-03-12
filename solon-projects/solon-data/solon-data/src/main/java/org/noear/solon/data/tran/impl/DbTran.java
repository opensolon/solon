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
package org.noear.solon.data.tran.impl;

import org.noear.solon.Utils;
import org.noear.solon.core.util.RunnableEx;
import org.noear.solon.data.annotation.Transaction;
import org.noear.solon.data.datasource.RoutingDataSourceMapping;
import org.noear.solon.data.tran.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据事务
 *
 * @author noear
 * @since 1.0
 * */
public abstract class DbTran extends DbTranNode implements TranNode {
    private final Transaction meta;
    private final Map<DataSource, Connection> conMap = new HashMap<>();
    private final TranListenerSet listenerSet = new TranListenerSet();

    //事务状态
    private int status = TranListener.STATUS_UNKNOWN;

    /**
     * 监听
     */
    public void listen(TranListener listener) {
        listenerSet.add(listener);

        //到这里说明事务已经开始干活了；开始执行提前之前的事件
//        listener.beforeCommit(tran.getMeta().readOnly());
    }

    public Transaction getMeta() {
        return meta;
    }

    public Connection getConnection(DataSource ds) throws SQLException {
        //支持动态数据源
        RoutingDataSourceMapping dsMapping = TranManager.routingGet(ds);

        if (dsMapping != null) {
            //支持"深度"动态数据源事务管理
            return getConnection(dsMapping.determineCurrentTarget(ds));
        } else {
            if (conMap.containsKey(ds)) {
                return conMap.get(ds);
            } else {
                Connection con = ds.getConnection();
                con.setAutoCommit(false);
                con.setReadOnly(meta.readOnly());
                if (meta.isolation().level > 0) {
                    con.setTransactionIsolation(meta.isolation().level);
                }

                conMap.putIfAbsent(ds, con);
                return con;
            }
        }
    }

    public DbTran(Transaction meta) {
        this.meta = meta;
    }

    public void execute(RunnableEx runnable) throws Throwable {

        try {
            //conMap 此时，还是空的
            //
            TranManager.currentSet(this);

            //conMap 会在run时产生
            //
            runnable.run();

            if (parent == null) {
                commit();
            }
        } catch (Throwable ex) {
            if (parent == null || meta.policy() == TranPolicy.nested) {
                rollback();
            }

            throw Utils.throwableUnwrap(ex);
        } finally {
            TranManager.currentRemove();

            if (parent == null) {
                close();
            }
        }
    }

    @Override
    public void commit() throws Throwable {
        //提前前
        listenerSet.beforeCommit(meta.readOnly());
        listenerSet.beforeCompletion();

        //提交
        super.commit();

        for (Map.Entry<DataSource, Connection> kv : conMap.entrySet()) {
            kv.getValue().commit();
        }

        //提交后
        status = TranListener.STATUS_COMMITTED;
        TranManager.currentRemove(); //移除当前节点
        listenerSet.afterCommit();
    }

    @Override
    public void rollback() {
        super.rollback();
        for (Map.Entry<DataSource, Connection> kv : conMap.entrySet()) {
            try {
                kv.getValue().rollback();
            } catch (Throwable e) {
                log.warn("Rollback failure", e);
            }
        }

        //回滚后
        status = TranListener.STATUS_ROLLED_BACK;
    }

    @Override
    public void close() throws Throwable {
        try {
            super.close();
            for (Map.Entry<DataSource, Connection> kv : conMap.entrySet()) {
                try {
                    if (kv.getValue().isClosed() == false) {
                        kv.getValue().close();
                        //
                        // close 后，链接池会对 autoCommit,readOnly 状态进行还原
                        //
                    }
                } catch (Throwable e) {
                    log.warn(e.getMessage(), e);
                }
            }
        } finally {
            //关闭后（完成后）
            listenerSet.afterCompletion(status);
        }
    }
}