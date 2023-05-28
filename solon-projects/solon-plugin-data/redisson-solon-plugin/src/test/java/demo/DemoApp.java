package demo;

import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.redisson.api.RedissonClient;

public class DemoApp {
    public static void main(String[] args){
        Solon.start(DemoApp.class, args);

        RedissonClient demo1 = Solon.context().getBean(RedissonClient.class);
        demo1.getAtomicDouble("test").set(0);
        demo1.getAtomicDouble("test").incrementAndGet();
        assert demo1.getAtomicDouble("test").get() == 1;
        System.out.println(demo1.getAtomicDouble("test").get());

        RedissonClient demo2 = Solon.context().getBean("demo2");
        demo2.getAtomicDouble("test").set(0);
        demo2.getAtomicDouble("test").incrementAndGet();
        assert demo2.getAtomicDouble("test").get() == 1;
        System.out.println(demo2.getAtomicDouble("test").get());
    }
}
