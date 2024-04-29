package demo.server;


import org.noear.solon.Solon;
import org.noear.solon.net.annotation.ServerEndpoint;
import org.noear.solon.net.stomp.StompUtil;
import org.noear.solon.net.stomp.ToStompWebSocketListener;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * stomp server 必须
 *
 * @author noear
 * @since 2.4
 */
@ServerEndpoint("/chat")
public class ChatToStompWebSocketListener extends ToStompWebSocketListener {

    //示例，非必须
    private static Timer timer = new Timer();
    //示例，非必须
    private static AtomicInteger atomicInteger = new AtomicInteger();

    public ChatToStompWebSocketListener() {
        super();
        //此处仅为示例，实际按需扩展，可以不添加
        Solon.context().getBeanAsync(CustomStompListenerImpl.class, bean -> {
            this.addListener(bean);
        });
        //this.addListener(new CustomStompListenerImpl());
        //此处仅为服务发送消息示例，可以不添加；间隔3秒发一次
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                StompUtil.send("/topic/todoTask1/" + atomicInteger.incrementAndGet(), "我来自服务端");
            }
        }, 3000, 3000);
    }
}