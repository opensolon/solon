package demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.annotation.Inject;
import org.noear.solon.extend.redisx.RedisClient;
import org.noear.solon.extend.redisx.utils.RedisCache;
import org.noear.solon.extend.redisx.utils.RedisQueue;
import org.noear.solon.extend.redisx.utils.RedisBus;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

/**
 * @author noear 2021/10/12 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(DemoApp.class)
public class DemoTest {
    @Inject
    RedisClient client;

    @Test
    public void test1() {
        //::redisX 基础接口使用

        client.open0(session -> {
            session.key("order:1").expire(10).set("hello");
        });

        String item_1 = client.open1(session -> {
            return session.key("order:1").get();
        });

        assert item_1 != null;

        client.open0(session -> {
            session.key("user:1").expire(10)
                    .hashSet("name", "noear")
                    .hashSet("sex", "1");
        });

        assert true;
    }

    @Test
    public void test2_cache() throws Exception {
        //::redisX 快捷功能使用

        //--- cache 使用
        RedisCache cache = client.getCache();
        cache.store("item:1", "hello", 2);

        assert "hello".equals(cache.get("item:1"));

        Thread.sleep(3 * 1000);

        assert "hello".equals(cache.get("item:1")) == false;
    }

    @Test
    public void test3_id() {
        //::redisX 快捷功能使用

        //--- id 使用
        long user_id = 10000 + client.getId("id:user").generate();
        long order_id = 1000000 + client.getId("id:order").generate();

        assert user_id > 10000;
        assert order_id > 1000000;
    }

    @Test
    public void test4_lock() {
        //::redisX 快捷功能使用

        //--- lock 锁使用
        if (client.getLock("user:121212").tryLock()) {
            assert true;
            //业务处理
        } else {
            assert false;
            //提示：请不要频繁提交
        }
    }

    @Test
    public void test5_queue() {
        //::redisX 快捷功能使用

        //--- lock 锁使用
        RedisQueue queue = client.getQueue("queue:test");
        queue.add("1");
        queue.add("2");

        assert "1".equals(queue.pop());
        assert "2".equals(queue.pop());
        assert queue.pop() == null;

        queue.add("3");
        queue.add("4");

        queue.popAll(item -> {
            System.out.println("test5_queue: " + item);
        });
    }

    @Test
    public void test6_topic() {
        //::redisX 快捷功能使用

        //--- topic 锁使用
        RedisBus bus = client.getBus();


        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(100);
                    bus.publish("topic:test", "event-" + System.currentTimeMillis());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        //这个函数，会卡死（如果没有订阅者，消息会白发）
        bus.subscribe((topic, message) -> {
            System.out.println(topic + " = " + message);
        }, "topic:test");
    }
}
