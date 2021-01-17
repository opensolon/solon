package org.noear.solon.cloud.extend.water.integration;

import org.noear.solon.Solon;
import org.noear.water.WaterClient;
import org.noear.water.utils.TextUtils;
import org.noear.solon.cloud.extend.water.WaterProps;

public class WaterAdapterImp extends WaterAdapter {

    private String _msg_receiver_url = null;

    public WaterAdapterImp() {
        String host = WaterProps.instance.getDiscoveryHostname();

        if (TextUtils.isEmpty(host)) {
            return;
        }

        String host_old = host;
        if (host.startsWith("@")) {
            host = WaterClient.Config.getByTagKey(host.substring(1)).value;
        }

        host = host.trim();

        if (TextUtils.isEmpty(host)) {
            throw new RuntimeException("Configuration " + host_old + " could not be found");
        }

        if (host.indexOf("://") < 0) {
            host = "http://" + host;
        }

        if (host.endsWith("/")) {
            _msg_receiver_url = host + "msg/receive";
        } else {
            _msg_receiver_url = host + "/msg/receive";
        }
    }

    @Override
    public String msg_receiver_url() {
        return _msg_receiver_url;
    }

    @Override
    public String alarm_mobile() {
        return "";
    }

    @Override
    public String service_name() {
        return Solon.cfg().appName();
    }

    @Override
    public String service_tag() {
        return Solon.cfg().appGroup();
    }

    @Override
    public String service_secretKey() {
        return WaterProps.service_secretKey;
    }


}
