package ch.qos.logback.solon;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

import java.util.Map;

/**
 * @author 颖
 * @since 1.6
 */
public class SolonTagsConverter extends MessageConverter {

    @Override
    public String convert(ILoggingEvent event) {
        Map<String, String> tagMap = event.getMDCPropertyMap();
        StringBuilder buf = new StringBuilder();
        if (tagMap.containsKey("tag0")) {
            buf.append("[@tag0:").append(tagMap.get("tag0")).append("]");
        }

        if (tagMap.containsKey("tag1")) {
            buf.append("[@tag1:").append(tagMap.get("tag1")).append("]");
        }

        if (tagMap.containsKey("tag2")) {
            buf.append("[@tag2:").append(tagMap.get("tag2")).append("]");
        }

        if (tagMap.containsKey("tag3")) {
            buf.append("[@tag3:").append(tagMap.get("tag3")).append("]");
        }

        if (tagMap.containsKey("tag4")) {
            buf.append("[@tag4:").append(tagMap.get("tag4")).append("]");
        }

        return buf.toString();
    }
}
