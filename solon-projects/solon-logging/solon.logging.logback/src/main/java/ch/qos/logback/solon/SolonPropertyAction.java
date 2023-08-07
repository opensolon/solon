package ch.qos.logback.solon;

import ch.qos.logback.core.joran.action.BaseModelAction;
import ch.qos.logback.core.joran.spi.SaxEventInterpretationContext;
import ch.qos.logback.core.model.Model;
import org.xml.sax.Attributes;

/**
 * @author noear
 * @since 1.6
 * @since 2.3
 */
public class SolonPropertyAction extends BaseModelAction {

    private static final String SOURCE_ATTRIBUTE = "source";

    private static final String DEFAULT_VALUE_ATTRIBUTE = "defaultValue";

    @Override
    protected Model buildCurrentModel(SaxEventInterpretationContext interpretationContext, String name,
                                      Attributes attributes) {
        SolonPropertyModel model = new SolonPropertyModel();
        model.setName(attributes.getValue(NAME_ATTRIBUTE));
        model.setSource(attributes.getValue(SOURCE_ATTRIBUTE));
        model.setScope(attributes.getValue(SCOPE_ATTRIBUTE));
        model.setDefaultValue(attributes.getValue(DEFAULT_VALUE_ATTRIBUTE));
        return model;
    }
}
