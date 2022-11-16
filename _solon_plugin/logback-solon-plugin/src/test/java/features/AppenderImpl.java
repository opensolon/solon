package features;

import org.noear.snack.ONode;
import org.noear.snack.core.Feature;
import org.noear.solon.logging.event.AppenderBase;
import org.noear.solon.logging.event.LogEvent;

/**
 * @author noear 2022/11/16 created
 */
public class AppenderImpl extends AppenderBase {

    @Override
    public void append(LogEvent logEvent) {
        System.err.println(ONode.load(logEvent, Feature.EnumUsingName).toJson());
    }
}
