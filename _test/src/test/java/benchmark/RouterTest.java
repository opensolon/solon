package benchmark;

import org.junit.Test;
import org.noear.solon.XRouter;
import org.noear.solon.core.XEndpoint;
import org.noear.solon.core.XMethod;

import java.util.Collections;
import java.util.Comparator;

public class RouterTest {
    @Test
    public void test1() {
        XRouter router = new XRouter();

        router.add("@@", XEndpoint.before, XMethod.ALL, 0, (x) -> x.output("000"));
        router.add("@@", XEndpoint.before, XMethod.ALL, 1, (x) -> x.output("111"));
        router.add("@@", XEndpoint.before, XMethod.ALL, 2, (x) -> x.output("222"));

        assert router.atBefore().size() == 3;

        long start = System.currentTimeMillis();
        for (int i = 1; i < 100000000; i++) {
            router.atBefore();
        }

        long times = System.currentTimeMillis() - start; //1百万，270秒

        System.out.println("times - " + times);
    }

    @Test
    public void test2() {
        XRouter router = new XRouter();

        router.add("@@", XEndpoint.before, XMethod.ALL, 0, (x) -> x.output("000"));
        router.add("@@", XEndpoint.before, XMethod.ALL, 1, (x) -> x.output("111"));
        router.add("@@", XEndpoint.before, XMethod.ALL, 2, (x) -> x.output("222"));

        assert router.atAfter().size() == 0;

        long start = System.currentTimeMillis();
        for (int i = 1; i < 1000000; i++) {
            router.atAfter();
        }

        long times = System.currentTimeMillis() - start;

        System.out.println("times - " + times);
    }

    @Test
    public void test3() throws Exception{
        XRouter router = new XRouter();

        router.add("@@", XEndpoint.before, XMethod.ALL, 0, (x) -> x.output("000"));
        router.add("@@", XEndpoint.before, XMethod.ALL, 1, (x) -> x.output("111"));
        router.add("@@", XEndpoint.before, XMethod.ALL, 2, (x) -> x.output("222"));

        new Thread(() -> {
            for (int i = 1; i < 1000000; i++) {
                XRouter.XListenerList tmp = new XRouter.XListenerList(router.atBefore());
                tmp.sort(Comparator.comparing(l -> l.index));

                //router.atBefore(tmp);
            }
        }).start();

        new Thread(() -> {
            for (int i = 1; i < 1000000; i++) {

                router.atBefore().forEach(l -> {

                });
            }
        }).start();

        System.in.read();
    }

}
