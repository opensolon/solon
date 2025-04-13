package labs;

import org.junit.jupiter.api.Test;
import org.noear.solon.Utils;
import org.noear.solon.core.Props;

/**
 * @author noear 2025/4/13 created
 */
public class PropsTest {
    @Test
    public void case1(){
      Props props = Utils.loadProps("demo.properties");
    }
}
