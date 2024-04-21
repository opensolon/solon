package demo;


import cn.hutool.core.codec.Base64;
import cn.hutool.cron.timingwheel.SystemTimer;
import cn.hutool.cron.timingwheel.TimerTask;
import com.ejlchina.okhttps.OkHttps;
import com.ejlchina.okhttps.WHttpTask;
import com.ejlchina.stomp.Header;
import com.ejlchina.stomp.Stomp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class StompClientTest {
    static final Logger log = LoggerFactory.getLogger(StompClientTest.class);

    public static void main(String[] args) throws Exception {
        WHttpTask whttpTask = OkHttps.webSocket("ws://127.0.0.1:8080/websocket/chat?token=" + Base64.encode("login=test&passcode=123456"))
                .heatbeat(5, 5);
        List<Header> headers = new ArrayList<>();
        headers.add(new Header("resource", "test"));
        conn(whttpTask, headers, 1, (stompSession) -> {
            stompSession.subscribe("/topic/todoTask1/*", new ArrayList<>(), msg -> {
                log.info("{}", msg);
            });
        });
    }

    private static AtomicReference<Stomp> stompAtomicReference = new AtomicReference<>();

    private static AtomicInteger atomicInteger = new AtomicInteger(0);

    private static SystemTimer systemTimer = new SystemTimer();

    static {
        systemTimer.start();
    }

    private static void conn(WHttpTask whttpTask, List<Header> headers, int count, Consumer<Stomp> onConnected) {
        log.info("Init webSocket client index {}", count);
        Stomp.over(whttpTask).setOnConnected(stomp -> {
            log.info("webSocket 连接成功");
            stompAtomicReference.set(stomp);
            atomicInteger.set(0);
            onConnected.accept(stomp);
        }).setOnDisconnected(c -> {
            log.info("webSocket 连接已断开 {}", c.toString());
            stompAtomicReference.set(null);
            //延迟重连，最大延迟30秒；连接成功后重置该值
            systemTimer.addTask(new TimerTask(() -> {
                //断线重连
                conn(whttpTask, headers, count + 1, onConnected);
            }, (atomicInteger.get() < 31 ? atomicInteger.incrementAndGet() : atomicInteger.get()) * 1000));
        }).setOnError(msg -> {
            log.info("webSocket OnError {}", msg.getPayload());
        }).setOnException(e -> {
            log.info("webSocket OnException {}", e.getMessage());
        }).connect(headers);
    }
}