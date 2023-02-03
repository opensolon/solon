package ch.qos.logback.solon;

import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.ElementSelector;
import ch.qos.logback.core.joran.spi.RuleStore;

/**
 * @author noear
 * @since 1.6
 */
public class SolonConfigurator extends JoranConfigurator {
    @Override
    public void addInstanceRules(RuleStore rs) {
        super.addInstanceRules(rs);
        rs.addRule(new ElementSelector("configuration/solonProperty"), new SolonPropertyAction());
    }
}
