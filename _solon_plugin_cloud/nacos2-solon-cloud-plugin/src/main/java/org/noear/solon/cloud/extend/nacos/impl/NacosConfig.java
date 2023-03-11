package org.noear.solon.cloud.extend.nacos.impl;

import com.alibaba.nacos.api.annotation.NacosProperties;
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

            properties.put(NacosProperties.SERVER_ADDR, sevAndCxt[0]);
            properties.put(NacosProperties.CONTEXT_PATH, sevAndCxt[1]);
        }else {
            properties.put(NacosProperties.SERVER_ADDR, server);
        }

        if (Utils.isNotEmpty(username)) {
            properties.put(NacosProperties.USERNAME, username);
        }

        if (Utils.isNotEmpty(password)) {
            properties.put(NacosProperties.PASSWORD, password);
        }

        if (Utils.isNotEmpty(Solon.cfg().appNamespace())) {
            properties.put(NacosProperties.NAMESPACE, Solon.cfg().appNamespace());
        }

        return properties;
    }
}
