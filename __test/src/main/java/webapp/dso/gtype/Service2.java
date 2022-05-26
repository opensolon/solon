package webapp.dso.gtype;

import org.noear.solon.annotation.Component;

/**
 * @author noear 2022/1/17 created
 */
@Component
public class Service2 implements Service {
    public int hello() {
       return 2;
    }
}