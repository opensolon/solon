package ch.qos.logback.solon.integration;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.solon.SolonConfigurator;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.runtime.NativeDetector;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.bean.InitializingBean;
import org.noear.solon.core.util.LogUtil;
import org.noear.solon.core.util.ResourceUtil;
import org.noear.solon.logging.LogOptions;
import org.noear.solon.logging.model.LoggerLevelEntity;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author noear
 * @since 1.6
 */
public class XPluginImp implements Plugin , InitializingBean {
    @Override
    public void afterInjection() throws Throwable {
        //尝试从配置里获取
        URL url = getUrlOfConfig();

        //尝试包内定制加载
        if (url == null) {
            //检查是否有原生配置文件
            if (ResourceUtil.hasResource("logback.xml")) {
                //如果有直接返回（不支持对它进行 Solon 扩展）
                return;
            }
        }

        //1::尝试应用环境加载
        if (url == null) {
            if (Utils.isNotEmpty(Solon.cfg().env())) {
                url = ResourceUtil.getResource("logback-solon-" + Solon.cfg().env() + ".xml");
            }
        }

        //2::尝试应用加载
        if (url == null) {
            url = ResourceUtil.getResource("logback-solon.xml");
        }

        //3::尝试默认加载
        if (url == null) {
            boolean fileEnable = Solon.cfg().getBool("solon.logging.appender.file.enable", true);

            if (fileEnable) {
                url = ResourceUtil.getResource("META-INF/solon_def/logback-def.xml");
            } else {
                url = ResourceUtil.getResource("META-INF/solon_def/logback-def_nofile.xml");
            }
        }

        initDo(url);
    }

    /**
     * 基于配置，获取日志配置文件
     */
    private URL getUrlOfConfig() throws MalformedURLException {
        String logConfig = Solon.cfg().get("solon.logging.config");

        if (Utils.isNotEmpty(logConfig)) {
            File logConfigFile = new File(logConfig);
            if (logConfigFile.exists()) {
                return logConfigFile.toURI().toURL();
            } else {
                LogUtil.global().warn("Props: No log config file: " + logConfig);
            }
        }

        return null;
    }

    @Override
    public void start(AopContext context) throws Throwable {
        //容器加载完后，允许再次处理
        afterInjection();
    }

    private void initDo(URL url) {
        if (url == null) {
            return;
        }

        try {
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            SolonConfigurator configurator = new SolonConfigurator();
            configurator.setContext(loggerContext);
            if (!NativeDetector.inNativeImage()) {
                loggerContext.reset(); // native 启动时，这里会线程卡死，先跳过
            }
            configurator.doConfigure(url);

            //同步 logger level 配置
            if (LogOptions.getLoggerLevels().size() > 0) {
                for (LoggerLevelEntity lle : LogOptions.getLoggerLevels()) {
                    Logger logger = loggerContext.getLogger(lle.getLoggerExpr());
                    logger.setLevel(Level.valueOf(lle.getLevel().name()));
                }
            }
        } catch (JoranException e) {
            throw new IllegalStateException(e);
        }
    }
}