package org.noear.solon.admin.client.utils;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.utils.LocalUtils;
import org.noear.solon.core.Signal;

/**
 * @author shaokeyibb
 * @since 2.3
 */
public class NetworkUtils {

    public static String getHostAndPort() {
        Signal signal = Solon.app().signalGet(Solon.cfg().serverPort());

        if (Utils.isEmpty(signal.host())) {
            return signal.protocol() + "://" + LocalUtils.getLocalAddress() + ":" + signal.port();
        } else {
            return signal.protocol() + "://" + signal.host() + ":" + signal.port();
        }
    }

}
