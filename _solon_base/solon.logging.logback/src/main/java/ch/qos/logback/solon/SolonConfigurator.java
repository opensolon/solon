package ch.qos.logback.solon;

import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.ElementSelector;
import ch.qos.logback.core.joran.spi.RuleStore;
import ch.qos.logback.core.model.processor.DefaultProcessor;

/**
 * @author noear
 * @since 1.6
 * @since 2.3
 */
public class SolonConfigurator extends JoranConfigurator {
    @Override
    protected void addModelHandlerAssociations(DefaultProcessor defaultProcessor) {
        defaultProcessor.addHandler(SolonPropertyModel.class,
                (handlerContext, handlerMic) -> new SolonPropertyModelHandler(this.context));

        super.addModelHandlerAssociations(defaultProcessor);
    }

    @Override
    public void addElementSelectorAndActionAssociations(RuleStore ruleStore) {
        super.addElementSelectorAndActionAssociations(ruleStore);
        ruleStore.addRule(new ElementSelector("configuration/solonProperty"),  SolonPropertyAction::new);
    }
}
