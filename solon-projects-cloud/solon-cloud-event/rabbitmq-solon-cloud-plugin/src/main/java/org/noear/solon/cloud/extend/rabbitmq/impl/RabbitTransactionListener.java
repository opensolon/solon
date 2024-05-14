package org.noear.solon.cloud.extend.rabbitmq.impl;

import com.rabbitmq.client.Channel;
import org.noear.solon.cloud.model.EventTranListener;

/**
 * @author noear
 * @since 2.8
 */
public class RabbitTransactionListener implements EventTranListener {
    private Channel transaction;

    public Channel getTransaction() {
        return transaction;
    }

    public RabbitTransactionListener(Channel transaction) {
        this.transaction = transaction;
    }

    @Override
    public void onCommit() throws Exception {
        transaction.txCommit();
    }

    @Override
    public void onRollback() throws Exception{
        transaction.txRollback();
    }
}
