package org.noear.solon.cloud.extend.zookeeper.impl;

import org.apache.zookeeper.*;
import org.noear.solon.Utils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @author noear
 * @since 1.3
 */
public class ZooKeeperWrap {
    private CountDownLatch latch = new CountDownLatch(1);
    private String server;
    private int sessionTimeout;
    private ZooKeeper real;

    public ZooKeeperWrap(String server, int sessionTimeout) {
        this.server = server;
        this.sessionTimeout = sessionTimeout;
    }

    public synchronized void connectServer() {
        if (real != null) {
            return;
        }

        connectServer0();
    }

    private void connectServer0(){
        try {
            real = new ZooKeeper(server, sessionTimeout, event->{
                switch (event.getState()){
                    case SyncConnected:
                        latch.countDown();
                        break;
                    case Expired:
                    case Disconnected:
                        connectServer0();
                        break;
                }
            });
            latch.await();
        } catch (Exception e) {
            throw Utils.throwableWrap(e);
        }
    }

    /**
     * 创建节点
     */
    public void createNode(String path, String data) throws Exception {
        real.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
    }

    /**
     * 移徐节点
     */
    public void removeNode(String path) throws Exception {
        real.delete(path, -1);
    }

    public void setNodeData(String path, String data) throws Exception {
        if (real.exists(path, false) == null) {
            createNode(path, data);
        } else {
            real.setData(path, data.getBytes(), 1);
        }
    }

    /**
     * 获取节点数据
     */
    public String getNodeData(String path) throws Exception {
        byte[] bytes = real.getData(path, false, null);
        return new String(bytes);
    }

    public void watchNodeData(String path, Watcher watcher) throws Exception {
        real.getData(path, event -> {
            if (event.getType() == Watcher.Event.EventType.NodeDataChanged) {
                watcher.process(event);
            }
        }, null);
    }

    /**
     * 查找节点
     */
    public Map<String, String> findChildrenNode(String parentPath) throws Exception {
        //获取子节点列表
        List<String> nodeList = real.getChildren(parentPath, null);

        Map<String, String> nodeListData = new LinkedHashMap<>();

        for (String node : nodeList) {
            String nodeData = getNodeData(parentPath + "/" + node);
            nodeListData.put(node, nodeData);
        }

        return nodeListData;
    }

    /**
     * 监视节点
     */
    public void watchChildrenNode(String parentPath, Watcher watcher) throws Exception {
        //获取子节点列表
        real.getChildren(parentPath, event -> {
            if (event.getType() == Watcher.Event.EventType.NodeChildrenChanged) {
                //发生子节点变化时再次调用此方法更新服务地址
                watcher.process(event);
            }
        });
    }

    public void close() throws InterruptedException {
        real.close();
    }
}
