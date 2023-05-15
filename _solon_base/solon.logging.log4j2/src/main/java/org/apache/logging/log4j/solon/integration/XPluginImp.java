package org.apache.logging.log4j.solon.integration;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.fusesource.jansi.AnsiConsole;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.bean.InitializingBean;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.core.util.JavaUtil;
import org.noear.solon.core.util.LogUtil;
import org.noear.solon.core.util.ResourceUtil;
import org.noear.solon.logging.LogOptions;
import org.noear.solon.logging.model.LoggerLevelEntity;

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
        if (JavaUtil.IS_WINDOWS && Solon.cfg().isFilesMode() == false) {
            //只在 window 用 jar 模式下才启用
            if (ClassUtil.hasClass(() -> AnsiConsole.class)) {
                AnsiConsole.systemInstall();
            }
        }

        //尝试从配置里获取
        URL url = getUrlOfConfig();

        //尝试包内定制加载
        if (url == null) {
            //检查是否有原生配置文件
            if (ResourceUtil.hasResource("log4j2.xml")) {
                //如果有直接返回（不支持对它进行 Solon 扩展）
                return;
            }
        }

        //1::尝试应用环境加载
        if (url == null) {
            //尝试环境加载
            if (Utils.isNotEmpty(Solon.cfg().env())) {
                url = ResourceUtil.getResource("log4j2-solon-" + Solon.cfg().env() + ".xml");
            }
        }

        //2::尝试应用加载
        if (url == null) {
            url = ResourceUtil.getResource("log4j2-solon.xml");
        }

        //3::尝试默认加载
        if (url == null) {
            boolean fileEnable = Solon.cfg().getBool("solon.logging.appender.file.enable", true);

            if (fileEnable) {
                url = ResourceUtil.getResource("META-INF/solon_def/log4j2-def.xml");
            } else {
                url = ResourceUtil.getResource("META-INF/solon_def/log4j2-def_nofile.xml");
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
            Configurator.reconfigure(url.toURI());

            //同步 logger level 配置
            if (LogOptions.getLoggerLevels().size() > 0) {
                LoggerContext context = LoggerContext.getContext(false);

                for (LoggerLevelEntity lle : LogOptions.getLoggerLevels()) {
                    LoggerConfig logger = new LoggerConfig(lle.getLoggerExpr(),
                            Level.valueOf(lle.getLevel().name()),
                            true);
                    context.getConfiguration().addLogger(logger.getName(), logger);
                }

                context.updateLoggers();
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
