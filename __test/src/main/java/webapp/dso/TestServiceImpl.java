package webapp.dso;

import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.ProxyComponent;

/**
 * @author noear 2022/10/2 created
 */
@ProxyComponent
public class TestServiceImpl implements TestService{

    @Inject
    EntityConfig entityConfig;

    @Override
    public void hello() {
        this.helloDo();
    }

    private void helloDo(){
        System.out.println("test");
    }
}
