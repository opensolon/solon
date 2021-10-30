package org.noear.solon.cloud.extend.rabbitmq;

import org.noear.solon.cloud.CloudProps;

/**
 * @author noear
 * @since 1.2
 */
public class RabbitmqProps {
    public static final String GROUP_SPLIT_MART = "::";

    public static final CloudProps instance = new CloudProps("rabbitmq");
}
