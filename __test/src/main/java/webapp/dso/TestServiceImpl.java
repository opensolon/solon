package webapp.dso;

import org.noear.solon.aspect.annotation.Service;

/**
 * @author noear 2022/10/2 created
 */
@Service
public class TestServiceImpl implements TestService{
    @Override
    public void hello() {
        this.helloDo();
    }

    private void helloDo(){
        System.out.println("test");
    }
}
