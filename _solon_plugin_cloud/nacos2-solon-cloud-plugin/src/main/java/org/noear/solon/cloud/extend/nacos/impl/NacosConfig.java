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
    public static Properties getServiceProperties(CloudProps cloudProps, String server){
        String username = cloudProps.getUsername();
        String password = cloudProps.getPassword();


        Properties properties = new Properties();
        if(server.contains("/")){
            String[] sevAndCxt = server.split("/");

            properties.put(PropertyKeyConst.SERVER_ADDR, sevAndCxt[0]);
            properties.put(PropertyKeyConst.CONTEXT_PATH, sevAndCxt[1]);
        }else {
            properties.put(PropertyKeyConst.SERVER_ADDR, server);
        }

        if (Utils.isNotEmpty(username)) {
            properties.put(PropertyKeyConst.USERNAME, username);
        }

        if (Utils.isNotEmpty(password)) {
            properties.put(PropertyKeyConst.PASSWORD, password);
        }

        if (Utils.isNotEmpty(Solon.cfg().appNamespace())) {
            properties.put(PropertyKeyConst.NAMESPACE, Solon.cfg().appNamespace());
        }

        return properties;
    }
}
