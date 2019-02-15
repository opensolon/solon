package org.noear.solon.extend.yaml;

import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.core.XPlugin;
import org.yaml.snakeyaml.Yaml;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        Yaml yaml = new Yaml();

        yaml.load(XUtil.getResource("/application.yml").toString());
    }
}
