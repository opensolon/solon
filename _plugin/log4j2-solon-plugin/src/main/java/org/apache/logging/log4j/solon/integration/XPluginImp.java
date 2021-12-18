package org.apache.logging.log4j.solon.integration;

import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.core.Plugin;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;

/**
 * @author noear
 * @since 1.6
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        URL url = Utils.getResource("log4j2.xml");
        if (url == null) {
            //尝试环境加载
            if (Utils.isNotEmpty(Solon.cfg().env())) {
                url = Utils.getResource("log4j2-solon-" + Solon.cfg().env() + ".xml");
            }

            //尝试应用加载
            if (url == null) {
                url = Utils.getResource("log4j2-solon.xml");
            }

            //尝试默认加载
            if (url == null) {
                url = Utils.getResource("META-INF/solon/logging/log4j2-def.xml");
            }

            if (url == null) {
                return;
            }

            initDo(url);
        }
    }

    private void initDo(URL url) {
        try {
            ConfigurationSource source = new ConfigurationSource(new FileInputStream(new File(url.getPath())), url);
            Configurator.initialize(null, source);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
