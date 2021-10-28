package org.noear.solon.extend.yaml;

import org.noear.solon.SolonApp;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.PropsLoader;

public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        if (PropsLoader.global() instanceof PropertiesLoader) {
            return;
        } else {
            //切换配置加载器
            PropsLoader.globalSet(PropertiesLoader.g);

            //尝试.yml的配置加载
            app.cfg().loadAdd("application.yml");
        }
    }
}
