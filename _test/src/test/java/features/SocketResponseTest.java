package features;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.XApp;
import org.noear.solon.boot.jdksocket._SocketSession;
import org.noear.solon.core.XMessage;
import org.noear.solon.core.XSession;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

import java.net.Socket;

@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(webapp.TestApp.class)
public class SocketResponseTest {
    @Test
    public void test() throws Throwable{
        String root = "tcp://localhost:" + (20000 + XApp.global().port());
        XMessage message =  XMessage.wrap(root + "/demog/中文/1", "Hello 世界!".getBytes());

        Socket socket = new Socket("localhost", XApp.global().port() + 20000);

        XSession session = _SocketSession.get(socket);
        XMessage rst = session.sendAndResponse(message);

        System.out.println(rst.toString());

        assert "我收到了：Hello 世界!".equals(rst.toString());
    }
}
