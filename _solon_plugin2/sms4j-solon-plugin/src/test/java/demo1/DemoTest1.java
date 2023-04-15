package demo1;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonJUnit5Extension;
import org.noear.solon.test.SolonTest;

//@Import(scanPackages = {"demo","demo1"})
@ExtendWith(SolonJUnit5Extension.class)
@SolonTest(DemoApp1.class)
public class DemoTest1 {

	@Inject
	private DemoAliSMSService demoAliSMSService;
	
	@Inject
	private DemoHuaweiSMSService demoHuaweiSMSService;
	
	@Test
	public void test() {
		demoAliSMSService.handle();
		demoHuaweiSMSService.handle();
	}
}
