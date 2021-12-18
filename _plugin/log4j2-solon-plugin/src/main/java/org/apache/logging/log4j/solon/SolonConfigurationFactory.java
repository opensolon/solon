package org.apache.logging.log4j.solon;

import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Order;
import org.apache.logging.log4j.core.config.plugins.Plugin;

/**
 * @author noear
 * @since 1.6
 */
@Plugin( name = "SolonConfigurationFactory",category = "ConfigurationFactory")
@Order(0)
public class SolonConfigurationFactory extends ConfigurationFactory {
    @Override
    protected String[] getSupportedTypes() {
        return new String[0];
    }

    @Override
    public Configuration getConfiguration(LoggerContext loggerContext, ConfigurationSource source) {
        return null;
    }
}
