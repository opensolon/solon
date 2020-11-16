package org.noear.solon.extend.properties.yaml;

import org.noear.solon.Solon;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.PropertiesLoader;

public class XPluginImp implements Plugin {
    @Override
    public void start(Solon app) {
        if (PropertiesLoader.global() instanceof org.noear.solon.extend.properties.yaml.PropertiesLoader) {
            return;
        } else {
            //切换配置加载器
            PropertiesLoader.globalSet(org.noear.solon.extend.properties.yaml.PropertiesLoader.g);

            //尝试.yml的配置加载
            app.prop().loadAdd("application.yml");
        }
    }
}
