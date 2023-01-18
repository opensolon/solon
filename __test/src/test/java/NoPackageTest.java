import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.test.SolonJUnit4ClassRunner;

/**
 * @author noear 2023/1/18 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
public class NoPackageTest {
    @Test
    public void error(){
        System.out.println("执行不到这儿来...");
    }
}
