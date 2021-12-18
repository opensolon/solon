package org.apache.logging.log4j.solon;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.core.pattern.PatternConverter;
import org.apache.logging.log4j.util.ReadOnlyStringMap;


/**
 * @author noear
 * @since 1.6
 */
@Plugin(name = "tags", category = PatternConverter.CATEGORY)
@ConverterKeys({"tags","tags"})
public class SolonTagsConverter extends LogEventPatternConverter {
    private static final SolonTagsConverter INSTANCE =
            new SolonTagsConverter();

    /**
     * 创建实体，这个函数必须有
     */
    public static SolonTagsConverter newInstance(
            final String[] options) {
        return INSTANCE;
    }

    private SolonTagsConverter() {
        super("tags", "tags");
    }

    @Override
    public void format(LogEvent event, StringBuilder toAppendTo) {
        ReadOnlyStringMap eData = event.getContextData();

        if (eData != null) {
            eData.forEach((tag, val) -> {
                if ("traceId".equals(tag) == false) {
                    toAppendTo.append("[@").append(tag).append(":").append(val).append("]");
                }
            });
        }
    }
}
