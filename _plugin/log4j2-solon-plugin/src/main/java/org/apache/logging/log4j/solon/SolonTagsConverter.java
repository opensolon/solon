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
     * */
    public static SolonTagsConverter newInstance(
            final String[] options) {
        return INSTANCE;
    }

    private SolonTagsConverter() {
        super("tags","tags");
    }

    @Override
    public void format(LogEvent event, StringBuilder toAppendTo) {
        ReadOnlyStringMap eData = event.getContextData();
        StringBuilder buf = toAppendTo;
        if (eData.containsKey("tag0")) {
            buf.append("[@tag0:").append(eData.getValue("tag0").toString()).append("]");
        }

        if (eData.containsKey("tag1")) {
            buf.append("[@tag1:").append(eData.getValue("tag1").toString()).append("]");
        }

        if (eData.containsKey("tag2")) {
            buf.append("[@tag2:").append(eData.getValue("tag2").toString()).append("]");
        }

        if (eData.containsKey("tag3")) {
            buf.append("[@tag3:").append(eData.getValue("tag3").toString()).append("]");
        }

        if (eData.containsKey("tag4")) {
            buf.append("[@tag4:").append(eData.getValue("tag4").toString()).append("]");
        }
    }
}
