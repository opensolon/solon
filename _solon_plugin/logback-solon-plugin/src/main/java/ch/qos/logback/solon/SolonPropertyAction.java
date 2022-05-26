package ch.qos.logback.solon;

import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.action.ActionUtil;
import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.util.OptionHelper;
import org.noear.solon.Solon;
import org.xml.sax.Attributes;

/**
 * @author noear
 * @since 1.6
 */
public class SolonPropertyAction extends Action {

    @Override
    public void begin(InterpretationContext context, String s, Attributes attrs) throws ActionException {
        String name = attrs.getValue("name");
        String source = attrs.getValue("source");
        ActionUtil.Scope scope = ActionUtil.stringToScope(attrs.getValue("scope"));
        String defaultValue = attrs.getValue("defaultValue");
        if (OptionHelper.isEmpty(name) || OptionHelper.isEmpty(source)) {
            this.addError("The \"name\" and \"source\" attributes of <springProperty> must be set");
        }

        ActionUtil.setProperty(context, name, this.getValue(source, defaultValue), scope);
    }

    private String getValue(String source, String defaultValue) {
        String value = Solon.cfg().getProperty(source);
        if (value != null) {
            return value;
        } else {
            int lastDot = source.lastIndexOf(46);
            if (lastDot > 0) {
                String prefix = source.substring(0, lastDot + 1);
                return Solon.cfg().getProperty(prefix + source.substring(lastDot + 1), defaultValue);
            } else {
                return defaultValue;
            }
        }
    }

    @Override
    public void end(InterpretationContext interpretationContext, String s) throws ActionException {

    }
}
