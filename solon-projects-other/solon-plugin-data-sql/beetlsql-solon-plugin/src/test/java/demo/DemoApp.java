package demo;

import org.noear.solon.Solon;
import org.noear.solon.annotation.SolonMain;

/**
 * @author noear 2021/7/12 created
 */
@SolonMain
public class DemoApp {
	public static void main(String[] args) {
		Solon.start(DemoApp.class, args, app -> {
			app.cfg().loadAdd("demo.yml");
		});
	}
}
