package features.solon.inject;

import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Managed;

/**
 * @author noear 2024/12/17 created
 */
@Managed
public class ComB {
    @Inject
    public IAExt ia;
}
