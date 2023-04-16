package org.tio.solon.config;

import org.tio.utils.time.Time;

/**
 * @since 2.2
 * */
public class TioServerProperties {

    /**
     * tio监听端口
     */
    private int port = 9420;

    /**
     * tio监听的ip
     */
    private String ip = "0.0.0.0";

    /**
     * 心跳超时时间(单位:毫秒)，超时会自动关闭连接
     */
    private long heartbeatTimeout;

    /**
     * 添加监控时段，不要添加过多的时间段，因为每个时间段都要消耗一份内存，一般加一个时间段就可以了
     */
    private Long[] ipStatDurations = {Time.MINUTE_1};

    /**
     * 上下文名称
     */
    private String name;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public long getHeartbeatTimeout() {
        return heartbeatTimeout;
    }

    public void setHeartbeatTimeout(long heartbeatTimeout) {
        this.heartbeatTimeout = heartbeatTimeout;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long[] getIpStatDurations() {
        return ipStatDurations;
    }

    public void setIpStatDurations(Long[] ipStatDurations) {
        this.ipStatDurations = ipStatDurations;
    }
}
