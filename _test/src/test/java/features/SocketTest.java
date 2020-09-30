package features;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.fairy.channel.SocketMessage;
import org.noear.fairy.channel.SocketUtils;
import org.noear.solon.XApp;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

import java.util.ArrayList;
import java.util.List;


@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(webapp.TestApp.class)
public class SocketTest {
    @Test
    public void test() throws Throwable {
        String root = "s://localhost:" + (20000 + XApp.global().port());


        SocketMessage msg = SocketUtils.send(root + "/demog/中文/1", "Hello 世界!");
        System.out.println(msg.toString());
        assert "我收到了：Hello 世界!".equals(msg.toString());

        Thread.sleep(100);
        msg = SocketUtils.send(root + "/demog/中文/1", "Hello 世界!");
        System.out.println(msg.toString());
        assert "我收到了：Hello 世界!".equals(msg.toString());

        Thread.sleep(100);
        msg = SocketUtils.send(root + "/demog/中文/2", "Hello 世界2!");
        System.out.println(msg.toString());
        assert "我收到了：Hello 世界2!".equals(msg.toString());

        Thread.sleep(100);

        msg = SocketUtils.send(root + "/demog/中文/3", "close");
        assert msg == null;
    }

    @Test
    public void test2() throws Throwable {
        //socket client
        String root = "s://localhost:" + (20000 + XApp.global().port());

        SocketUtils.send(root + "/seb/test", "Hello 世界!+1", (msg, err) -> {
            if (msg == null) {
                return;
            }
            System.out.println(msg.toString());
        });


        Thread.sleep(1000 * 2);
    }

    @Test
    public void test3() throws Throwable {
        //socket client
        String root = "s://localhost:" + (20000 + XApp.global().port());

        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(i);
        }

        list.parallelStream().forEach((i) -> {
            try {
                SocketUtils.send(root + "/seb/test", "Hello 世界!+" + i, (msg, err) -> {
                    if (msg == null) {
                        return;
                    }
                    System.out.println(msg.toString());
                });
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        });

        Thread.sleep(1000 * 2);
    }

    @Test
    public void test4() throws Throwable {
        //socket client
        String root = "s://localhost:" + (20000 + XApp.global().port());


        SocketUtils.create(root).send(root + "/seb/test", "Hello 世界!", (msg, err) -> {
            if (msg == null) {
                return;
            }
            System.out.println(msg.toString());
        });

        SocketUtils.create(root).send(root + "/seb/test", "Hello 世界!", (msg, err) -> {
            if (msg == null) {
                return;
            }
            System.out.println(msg.toString());
        });

        Thread.sleep(1000 * 2);
    }
}
