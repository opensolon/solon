package org.noear.solon.cloud.extend.rocketmq.impl;

import org.apache.rocketmq.client.apis.message.MessageView;
import org.apache.rocketmq.client.apis.producer.TransactionChecker;
import org.apache.rocketmq.client.apis.producer.TransactionResolution;

/**
 * @author noear
 * @since 2.8
 */
public class RocketmqTransactionCheckerDefault implements TransactionChecker {
    @Override
    public TransactionResolution check(MessageView messageView) {
        return TransactionResolution.UNKNOWN;
    }
}
