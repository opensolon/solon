package org.noear.solon.cloud.extend.water;

import org.noear.solon.Solon;
import org.noear.solon.cloud.CloudProps;

/**
 * @author noear
 * @since 1.2
 */
public class WaterProps {

    private static final String EVENT_RECEIVE = "solon.cloud.water.event.receive";

    public static final CloudProps instance = new CloudProps("water");


    public static String getEventReceive() {
        return Solon.cfg().get(EVENT_RECEIVE);
    }

    public static void setEventReceive(String value) {
        Solon.cfg().setProperty(EVENT_RECEIVE, value);
    }
}
