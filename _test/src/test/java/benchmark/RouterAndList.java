package benchmark;

import org.junit.Test;
import org.noear.solon.XRouter;
import org.noear.solon.core.XEndpoint;
import org.noear.solon.core.XMethod;

public class RouterAndList {
    @Test
    public void test1() {
        XRouter router = new XRouter();

        router.add("@@", XEndpoint.before, XMethod.ALL, 0, (x) -> x.output("000"));
        router.add("@@", XEndpoint.before, XMethod.ALL, 1, (x) -> x.output("111"));
        router.add("@@", XEndpoint.before, XMethod.ALL, 2, (x) -> x.output("222"));

        assert router.atBefore().size() == 3;

        long start = System.currentTimeMillis();
        for (int i = 1; i < 1000000; i++) {
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

        assert router.atAfter().size() == 3;

        long start = System.currentTimeMillis();
        for (int i = 1; i < 1000000; i++) {
            router.atAfter();
        }

        long times = System.currentTimeMillis() - start;

        System.out.println("times - " + times);
    }

}
