package features;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Init;

/**
 * @author noear 2021/12/17 created
 */
@Component
public class App {
    public static void main(String[] args){
        Solon.start(App.class, args);
    }

    @Init
    public void init(){
        throw new RuntimeException("测试一下");
    }
}
