package org.noear.solon.cloud.extend.rocketmq.impl;

import org.apache.rocketmq.client.apis.producer.Transaction;
import org.noear.solon.cloud.model.EventTranListener;

/**
 * @author noear
 * @since 2.8
 */
public class RocketmqTransactionListener implements EventTranListener {
    private Transaction transaction;

    public Transaction getTransaction() {
        return transaction;
    }

    public RocketmqTransactionListener(Transaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public void onCommit() throws Exception {
        transaction.commit();
    }

    @Override
    public void onRollback() throws Exception{
        transaction.rollback();
    }
}
