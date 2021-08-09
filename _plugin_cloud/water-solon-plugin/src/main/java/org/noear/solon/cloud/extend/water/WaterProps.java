package org.noear.solon.cloud.extend.water;

import org.noear.solon.Solon;
import org.noear.solon.cloud.CloudProps;

/**
 * @author noear
 * @since 1.2
 */
public class WaterProps {
    public static final String GROUP_SPLIT_MART = "::";

    static final String EVENT_RECEIVE = "solon.cloud.water.event.receive";
    static final String EVENT_SEAL = "solon.cloud.water.event.seal";

    public static final CloudProps instance = new CloudProps("water");

    public static String getEventSeal() {
        return Solon.cfg().get(EVENT_SEAL);
    }

    public static String getEventReceive() {
        return Solon.cfg().get(EVENT_RECEIVE);
    }

    public static void setEventReceive(String value) {
        Solon.cfg().setProperty(EVENT_RECEIVE, value);
    }
}
