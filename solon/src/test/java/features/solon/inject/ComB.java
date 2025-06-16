package features.solon.inject;

import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Component;

/**
 * @author noear 2024/12/17 created
 */
@Component
public class ComB {
    @Inject
    public IAExt ia;
}
