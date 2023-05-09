package demo1;

import java.io.UnsupportedEncodingException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonJUnit5Extension;
import org.noear.solon.test.SolonTest;
import org.tio.core.Tio;
import org.tio.server.TioServerConfig;

@ExtendWith(SolonJUnit5Extension.class)
@SolonTest(DemoApp1.class)
public class DemoTest1 {

	@Inject
	private TioServerConfig tioServerConfig;
	
	@Test
	public void test() throws UnsupportedEncodingException {
//		System.out.println(tioServerConfig);
		HelloPacket packet = new HelloPacket();
		packet.setBody("This message is pushed by Tio Server.".getBytes(HelloPacket.CHARSET));
		Tio.sendToAll(tioServerConfig, packet);
		System.out.println("Push a message to client successfully");
	}
}
