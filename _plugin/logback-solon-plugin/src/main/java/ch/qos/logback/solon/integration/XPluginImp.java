package ch.qos.logback.solon.integration;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.core.Plugin;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

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
            url = Utils.getResource("logback_def.xml");

            try {
                LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
                JoranConfigurator jc = new JoranConfigurator();
                jc.setContext(loggerContext);
                loggerContext.reset();
                jc.doConfigure(url);
            } catch (JoranException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
