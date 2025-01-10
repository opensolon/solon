package demo.solon.flow.case2;

import org.noear.solon.annotation.Component;
import org.noear.solon.flow.core.TaskComponent;

import java.util.Map;

/**
 * @author noear 2025/1/11 created
 */
@Component("b")
public class BCom implements TaskComponent {
    @Override
    public void run(Map<String, Object> model) throws Exception {
        System.out.println("BCom");
    }
}
