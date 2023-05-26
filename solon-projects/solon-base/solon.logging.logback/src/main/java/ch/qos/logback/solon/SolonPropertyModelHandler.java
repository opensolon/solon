package ch.qos.logback.solon;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.action.ActionUtil;
import ch.qos.logback.core.joran.action.ActionUtil.Scope;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.ModelUtil;
import ch.qos.logback.core.model.processor.ModelHandlerBase;
import ch.qos.logback.core.model.processor.ModelHandlerException;
import ch.qos.logback.core.model.processor.ModelInterpretationContext;
import ch.qos.logback.core.util.OptionHelper;
import org.noear.solon.Solon;

/**
 * @author noear
 * @since 2.3
 */
public class SolonPropertyModelHandler extends ModelHandlerBase {

    SolonPropertyModelHandler(Context context) {
        super(context);
    }

    @Override
    public void handle(ModelInterpretationContext intercon, Model model) throws ModelHandlerException {
        SolonPropertyModel propertyModel = (SolonPropertyModel) model;
        Scope scope = ActionUtil.stringToScope(propertyModel.getScope());
        String defaultValue = propertyModel.getDefaultValue();
        String source = propertyModel.getSource();
        if (OptionHelper.isNullOrEmpty(propertyModel.getName()) || OptionHelper.isNullOrEmpty(source)) {
            addError("The \"name\" and \"source\" attributes of <solonProperty> must be set");
        }
        ModelUtil.setProperty(intercon, propertyModel.getName(), getValue(source, defaultValue), scope);
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

}
