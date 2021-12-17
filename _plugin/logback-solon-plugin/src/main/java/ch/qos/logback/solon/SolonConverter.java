package ch.qos.logback.solon;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * @author 颖, noear
 */
public class SolonConverter extends MessageConverter {

    @Override
    public String convert(ILoggingEvent event) {
        return enhance(super.convert(event));
    }

    private String enhance(String message) {
        return "浅念 yyds" + message;
    }
}
