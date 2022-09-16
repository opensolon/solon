package org.apache.logging.log4j.solon;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.spi.StandardLevel;
import org.apache.logging.log4j.util.ReadOnlyStringMap;
import org.noear.solon.logging.AppenderHolder;
import org.noear.solon.logging.AppenderManager;
import org.noear.solon.logging.event.Level;

import java.io.Serializable;


/**
 * @author noear
 * @since 1.4
 */
@Plugin(name="Cloud", category="Core", elementType="appender", printObject=true)
public final  class SolonCloudAppender extends AbstractAppender {

    protected SolonCloudAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions) {
        super(name, filter, layout, ignoreExceptions, Property.EMPTY_ARRAY);
    }

    AppenderHolder appender;

    @Override
    public void append(LogEvent e) {
        if (appender == null) {
            appender = AppenderManager.getInstance().get("cloud");

            if (appender == null) {
                return;
            }
        }

        Level level;

        int eLevel = e.getLevel().intLevel();
        if (StandardLevel.DEBUG.intLevel() == (eLevel)) {
            level = Level.DEBUG;
        } else if (StandardLevel.WARN.intLevel() == (eLevel)) {
            level = Level.WARN;
        } else if (StandardLevel.INFO.intLevel() == (eLevel)) {
            level = Level.INFO;
        } else if (StandardLevel.TRACE.intLevel() == (eLevel)) {
            level = Level.TRACE;
        } else {
            level = Level.ERROR;
        }

        ReadOnlyStringMap eData = e.getContextData();

        org.noear.solon.logging.event.LogEvent event = new org.noear.solon.logging.event.LogEvent(
                e.getLoggerName(),
                level,
                (eData == null ? null : eData.toMap()),
                e.getMessage(),
                e.getTimeMillis(),
                e.getThreadName(),
                e.getThrown());

        appender.append(event);
    }

    @PluginFactory
    public static SolonCloudAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginElement("Layout") Layout<? extends Serializable> layout,
            @PluginElement("Filter") final Filter filter,
            @PluginAttribute("otherAttribute") String otherAttribute) {

        if (name == null) {
            LOGGER.error("No name provided for SolonCloudAppender");
            return null;
        }

        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }

        return new SolonCloudAppender(name, filter, layout, true);
    }
}
