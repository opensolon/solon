package org.tio.client.demo1;

import java.util.Date;

import org.noear.solon.Utils;
import org.tio.client.ClientChannelContext;
import org.tio.client.ReconnConf;
import org.tio.client.TioClient;
import org.tio.client.TioClientConfig;
import org.tio.client.intf.TioClientHandler;
import org.tio.client.intf.TioClientListener;
import org.tio.core.Node;
import org.tio.core.Tio;

import demo1.HelloPacket;

public class HelloClientStarter {
    //服务器节点
    public static Node serverNode = new Node(Const.SERVER, Const.PORT);
    //handler, 包括编码、解码、消息处理
    public static TioClientHandler tioClientHandler = new HelloClientAioHandler();
    //事件监听器，可以为null，但建议自己实现该接口，可以参考showcase了解些接口
    public static TioClientListener aioListener = null;
    //断链后自动连接的，不想自动连接请设为null
    private static ReconnConf reconnConf = new ReconnConf(5000L);
    //一组连接共用的上下文对象
    public static TioClientConfig clientTioConfig = new TioClientConfig(tioClientHandler, aioListener, reconnConf);
    public static TioClient tioClient = null;
    public static ClientChannelContext clientChannelContext = null;
    /**
     * 启动程序入口
     */
    public static void main(String[] args) throws Exception {
    	System.out.println("tio client start...");
        clientTioConfig.setHeartbeatTimeout(Const.TIMEOUT);
        tioClient = new TioClient(clientTioConfig);
        clientChannelContext = tioClient.connect(serverNode);
        //连上后，发条消息玩玩
        for(int i = 0;i<100;i++) {
        	System.out.println("tio client send index: "+i);
        	send();
        }
    }
    private static void send() throws Exception {
        HelloPacket packet = new HelloPacket();
        Date now = new Date();
        String msg = now.getTime()+", hello world! "+Utils.guid();
        packet.setBody(msg.getBytes(HelloPacket.CHARSET));
        Tio.send(clientChannelContext, packet);
    }
}