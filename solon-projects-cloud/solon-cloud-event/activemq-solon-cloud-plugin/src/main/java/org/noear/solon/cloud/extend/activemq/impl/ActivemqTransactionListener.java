package org.noear.solon.cloud.extend.activemq.impl;

import org.noear.solon.cloud.model.EventTranListener;

import javax.jms.Session;

/**
 * @author noear
 * @since 2.8
 */
public class ActivemqTransactionListener implements EventTranListener {
    private Session transaction;

    public Session getTransaction() {
        return transaction;
    }

    public ActivemqTransactionListener(Session transaction) {
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
