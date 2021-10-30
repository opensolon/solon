package org.noear.solon.cloud.extend.water;

import org.noear.solon.cloud.CloudProps;

/**
 * @author noear
 * @since 1.2
 */
public class WaterProps {
    public static final String GROUP_SPLIT_MART = "::";

    public static final String PROP_EVENT_seal = "event.seal";
    public static final String PROP_EVENT_receive = "event.receive";


    public static final CloudProps instance = new CloudProps("water");
}
