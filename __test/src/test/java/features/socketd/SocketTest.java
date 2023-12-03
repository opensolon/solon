package features.socketd;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.socketd.SocketD;
import org.noear.socketd.exception.SocketdException;
import org.noear.socketd.exception.SocketdTimeoutException;
import org.noear.socketd.transport.core.Entity;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.solon.Solon;
import org.noear.solon.test.SolonJUnit5Extension;
import org.noear.solon.test.SolonTest;
import webapp.App;

import java.util.ArrayList;
import java.util.List;


@ExtendWith(SolonJUnit5Extension.class)
@SolonTest(App.class)
public class SocketTest {
    @Test
    public void test_compatible() throws Throwable {
        //
        //这是短链接模式
        //
        String root = "tcp://localhost:" + (20000 + Solon.cfg().serverPort());
        Session session = SocketD.createClient(root).open();

        Entity msg = session.sendAndRequest(root + "/demog/中文/1", new StringEntity("Hello 世界!"));
        System.out.println(msg.dataAsString());
        assert "我收到了：Hello 世界!".equals(msg.dataAsString());

        Thread.sleep(100);
        msg = session.sendAndRequest(root + "/demog/中文/1", new StringEntity("Hello 世界!"));
        System.out.println(msg.toString());
        assert "我收到了：Hello 世界!".equals(msg.dataAsString());

        Thread.sleep(100);
        msg = session.sendAndRequest(root + "/demog/中文/2", new StringEntity("Hello 世界2!"));
        System.out.println(msg.toString());
        assert "我收到了：Hello 世界2!".equals(msg.dataAsString());

        Thread.sleep(100);

        try {
            msg = session.sendAndRequest(root + "/demog/中文/3", new StringEntity("close"));
            //assert msg == null;
            assert false;
        } catch (SocketdException e) {
            assert true;
        }

        Thread.sleep(100);
    }


    @Test
    public void test2() throws Throwable {
        //socket client
        String root = "tcp://localhost:" + (20000 + Solon.cfg().serverPort());
        Session session = SocketD.createClient(root).open();

        session.sendAndSubscribe(root + "/seb/test", new StringEntity("Hello 世界!+1"), (msg) -> {
            if (msg == null) {
                return;
            }
            System.out.println(msg.toString());
        });


        Thread.sleep(100);
    }

    @Test
    public void test3() throws Throwable {
        //socket client
        String root = "tcp://localhost:" + (20000 + Solon.cfg().serverPort());

        Session session = SocketD.createClient(root).open();

        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(i);
        }

        list.parallelStream().forEach((i) -> {
            try {
                session.sendAndSubscribe(root + "/seb/test", new StringEntity("Hello 世界!+" + i), (msg) -> {
                    if (msg == null) {
                        return;
                    }
                    System.out.println(msg.toString());
                });
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        });

        Thread.sleep(100);
    }

    @Test
    public void test4() throws Throwable {
        //socket client
        String root = "tcp://localhost:" + (20000 + Solon.cfg().serverPort());

        Session session = SocketD.createClient(root).open();


        session.sendAndSubscribe(root + "/seb/test", new StringEntity("Hello 世界!"), (msg) -> {
            if (msg == null) {
                return;
            }
            System.out.println(msg.toString());
        });

        session.sendAndSubscribe(root + "/seb/test", new StringEntity("Hello 世界!"), (msg) -> {
            if (msg == null) {
                return;
            }
            System.out.println(msg.toString());
        });

        Thread.sleep(100);
    }
}
