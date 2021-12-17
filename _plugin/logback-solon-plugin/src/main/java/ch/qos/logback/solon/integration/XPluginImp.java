package ch.qos.logback.solon.integration;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.solon.SolonConfigurator;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.EventBus;
import org.slf4j.LoggerFactory;

import java.net.URL;

/**
 * @author noear
 * @since 1.6
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        URL url = Utils.getResource("logback.xml");
        if (url == null) {
            //尝试环境加载
            if (Utils.isNotEmpty(Solon.cfg().env())) {
                url = Utils.getResource("logback-app-" + Solon.cfg().env() + ".xml");
            }

            //尝试应用加载
            if (url == null) {
                url = Utils.getResource("logback-app.xml");
            }

            //尝试默认加载
            if (url == null) {
                url = Utils.getResource("META-INF/solon/logging/logback-def.xml");
            }

            if (url == null) {
                return;
            }

            try {
                LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
                SolonConfigurator jc = new SolonConfigurator();
                jc.setContext(loggerContext);
                loggerContext.reset();
                jc.doConfigure(url);
            } catch (JoranException e) {
                EventBus.push(e);
            }
        }
    }
}
