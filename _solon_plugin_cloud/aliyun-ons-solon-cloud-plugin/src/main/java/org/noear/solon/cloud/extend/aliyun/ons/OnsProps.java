package org.noear.solon.cloud.extend.aliyun.ons;

import org.noear.solon.cloud.CloudProps;

/**
 * @author cgy
 * @since 1.11.3
 */
public class OnsProps {
    public static final String GROUP_SPLIT_MART = "--";

    public static final String PROP_EVENT_consumerGroup = "event.consumerGroup";
    public static final String PROP_EVENT_producerGroup = "event.producerGroup";

    public static final String PROP_EVENT_accessKey = "event.accessKey";

    public static final String PROP_EVENT_secretKey = "event.secretKey";

    public static final String PROP_EVENT_MessageModel = "event.messageModel";

    public static final String PROP_EVENT_EnableConsoleLog = "event.enableConsoleLog";

    public static final String PROP_EVENT_SendMsgTimeoutMillis = "event.sendMsgTimeoutMillis";

    public static final String PROP_EVENT_ConsumeThreadNums = "event.consumeThreadNums";

    public static final String PROP_EVENT_maxReconsumeTimes = "event.maxReconsumeTimes";


    public static final CloudProps instance = new CloudProps("aliyun.ons");
}
