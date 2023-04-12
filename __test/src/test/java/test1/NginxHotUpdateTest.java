package test1;

import org.junit.Test;
import org.noear.solon.test.HttpTester;

public class NginxHotUpdateTest extends HttpTester {
    @Test
    public void test1() throws Exception {
        new Thread(() -> {
            test1Do();
        }).start();

        System.in.read();
    }

    private void test1Do() {
        String url = "http://rock.sponge.io/getAppByID?appID=10970";

        while (true) {
            try {
                http(url).get();
                System.out.println(System.currentTimeMillis());
            } catch (Throwable ex) {
                ex.printStackTrace();
                break;
            }
        }
    }
}
