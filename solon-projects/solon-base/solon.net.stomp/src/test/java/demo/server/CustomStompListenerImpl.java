package demo.server;

import org.noear.solon.annotation.Component;
import org.noear.solon.net.stomp.IStompListener;
import org.noear.solon.net.websocket.WebSocket;

/**
 * 按需扩展，比是必须
 *
 * @author noear
 * @since 2.4
 */
@Component
public class CustomStompListenerImpl implements IStompListener {

    @Override
    public void onOpen(WebSocket socket) {
        String user = socket.param("user");
        System.out.println("建议放鉴权: " + user);
    }

    //@Override
    //public void onConnect(WebSocket socket, Message message) {
    //    System.out.println("按需扩展");
    //}
    //
    //@Override
    //public void onClose(WebSocket socket) {
    //    System.out.println("按需扩展");
    //}
    //
    //@Override
    //public void onDisconnect(WebSocket socket, Message message) {
    //    System.out.println("按需扩展");
    //}
    //
    //@Override
    //public void onSubscribe(WebSocket socket, Message message) {
    //    System.out.println("按需扩展");
    //
    //}
    //
    //@Override
    //public void onUnsubscribe(WebSocket socket, Message message) {
    //    System.out.println("按需扩展");
    //}
    //
    //@Override
    //public void onSend(WebSocket socket, Message message) {
    //    System.out.println("按需扩展");
    //}
    //
    //@Override
    //public void onAck(WebSocket socket, Message message) {
    //    System.out.println("按需扩展");
    //}
}