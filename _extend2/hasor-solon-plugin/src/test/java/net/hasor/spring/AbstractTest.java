package net.hasor.spring;
import net.hasor.test.spring.mod1.TestModuleA;
import net.hasor.test.spring.mod1.TestModuleB;
import net.hasor.test.spring.mod1.TestModuleC;
import net.hasor.test.spring.mod1.TestModuleD;
import org.junit.Before;

public class AbstractTest {
    @Before
    public void clear() {
        TestModuleA.reset();
        TestModuleB.reset();
        TestModuleC.reset();
        TestModuleD.reset();
    }
}
//  refProperties,
