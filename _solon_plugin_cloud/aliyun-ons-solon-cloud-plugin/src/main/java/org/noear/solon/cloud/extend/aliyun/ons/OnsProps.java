package org.noear.solon.cloud.extend.aliyun.ons;

import org.noear.solon.cloud.CloudProps;

/**
 * @author cgy
 * @since 1.11
 */
public class OnsProps {
    public static final String GROUP_SPLIT_MARK = "--";

    public static final String PROP_EVENT_consumerGroup = "event.consumerGroup";
    public static final String PROP_EVENT_producerGroup = "event.producerGroup";

    public static final String PROP_EVENT_consumeThreadNums = "event.consumeThreadNums";

    public static final String PROP_EVENT_maxReconsumeTimes = "event.maxReconsumeTimes";


    public static final CloudProps instance = new CloudProps("aliyun.ons");
}
