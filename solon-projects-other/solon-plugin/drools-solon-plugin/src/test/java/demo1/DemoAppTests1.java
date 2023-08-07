package demo1;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.api.runtime.KieSession;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonJUnit5Extension;
import org.noear.solon.test.SolonTest;

import com.drools.solon.KieTemplate;

@ExtendWith(SolonJUnit5Extension.class)
@SolonTest(DemoApp.class)
public class DemoAppTests1 {
	
	@Inject
	private KieTemplate kieTemplate;

	@Test
	public void test() {
		KieSession ks = kieTemplate.getKieSession("fifuNine.drl");

		System.out.println(ks);
	}
	
}
