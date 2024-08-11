package webapp.demo0_bean.construction1;

import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

/**
 * @author noear 2024/8/11 created
 */
@Component
public class Construction1Com11B {
    private Condition1B condition1B;

    public Construction1Com11B(@Inject("construction1-condition1B") Condition1B condition1B) {
        this.condition1B = condition1B;
    }

    public Condition1B getCondition1B() {
        return condition1B;
    }
}
