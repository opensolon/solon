package org.noear.solon.cloud.extend.nacos.impl;

import com.alibaba.nacos.api.PropertyKeyConst;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudProps;

import java.util.Properties;

/**
 * @author noear
 * @since 2.2
 */
public class NacosConfig {
    public static Properties getServiceProperties(CloudProps cloudProps, Properties properties, String server) {
        String username = cloudProps.getUsername();
        String password = cloudProps.getPassword();


        properties.putIfAbsent(PropertyKeyConst.SERVER_ADDR, server);

        if (Utils.isNotEmpty(username)) {
            properties.putIfAbsent(PropertyKeyConst.USERNAME, username);
        }

        if (Utils.isNotEmpty(password)) {
            properties.putIfAbsent(PropertyKeyConst.PASSWORD, password);
        }

        if (Utils.isNotEmpty(Solon.cfg().appNamespace())) {
            properties.putIfAbsent(PropertyKeyConst.NAMESPACE, Solon.cfg().appNamespace());
        }

        return properties;
    }
}
