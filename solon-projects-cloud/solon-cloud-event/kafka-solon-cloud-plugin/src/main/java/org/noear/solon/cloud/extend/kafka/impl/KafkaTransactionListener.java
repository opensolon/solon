package org.noear.solon.cloud.extend.kafka.impl;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.noear.solon.cloud.model.EventTranListener;

/**
 * @author noear
 * @since 2.8
 */
public class KafkaTransactionListener implements EventTranListener {
    private KafkaProducer<String, String> transaction;

    public KafkaProducer<String, String> getTransaction() {
        return transaction;
    }

    public KafkaTransactionListener(KafkaProducer<String, String> transaction) {
        this.transaction = transaction;
    }

    @Override
    public void onCommit() throws Exception {
        transaction.commitTransaction();
    }

    @Override
    public void onRollback() throws Exception{
        transaction.abortTransaction();
    }
}
