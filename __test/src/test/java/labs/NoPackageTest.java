package labs;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.solon.test.SolonJUnit5Extension;
import org.noear.solon.test.SolonTest;

/**
 * @author noear 2023/1/18 created
 */
@SolonTest
public class NoPackageTest {
    @Test
    public void error(){
        System.out.println("执行不到这儿来...");
    }
}
