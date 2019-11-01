package org.noear.solon.extend.yaml;

import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.core.XPlugin;
import org.noear.solon.core.XPropertiesLoader;

import java.util.Properties;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        XPropertiesLoader.global = PropertiesLoaderEx.g;

        Properties prop = XUtil.getProperties("application.yml");

        app.prop().putAll(prop);
    }
}
