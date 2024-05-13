package org.noear.solon.cloud.extend.folkmq.impl;

import org.noear.folkmq.client.MqTransaction;
import org.noear.solon.cloud.model.EventTransactionListener;

import java.io.IOException;

/**
 * @author noear
 * @since 2.8
 */
public class FolkmqTransactionListener implements EventTransactionListener {
    private MqTransaction transaction;

    public MqTransaction getTransaction() {
        return transaction;
    }

    public FolkmqTransactionListener(MqTransaction transaction) {
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
