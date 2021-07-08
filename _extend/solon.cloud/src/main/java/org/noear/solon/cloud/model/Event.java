package org.noear.solon.cloud.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 事件模型
 *
 * @author noear
 * @since 1.2
 */
public class Event implements Serializable {

    private  String topic;
    private  String content;
    private String key;
    private String tags;
    private Date scheduled;
    private int times;
    private transient String channel;
    private transient int qos = 1;
    private transient boolean retained = false;

    public Event(){
        //序列化需要
    }

    /**
     * @param topic 主题
     * @param content 内容
     * */
    public Event(String topic, String content) {
        this.topic = topic;
        this.content = content;
    }

    /**
     * 获取主题
     * */
    public String topic() {
        return topic;
    }

    /**
     * 获取内容
     * */
    public String content() {
        return content;
    }

    /**
     * 获取事件键
     * */
    public String key() {
        return key;
    }

    /**
     * 设置事件键
     * */
    public Event key(String key) {
        this.key = key;
        return this;
    }

    /**
     * 获取检索标签
     * */
    public String tags() {
        return tags;
    }

    /**
     * 设置检索标签
     * */
    public Event tags(String tags) {
        this.tags = tags;
        return this;
    }

    /**
     * 获取预定执行时间（派发时专用）
     * */
    public Date scheduled() {
        return scheduled;
    }

    /**
     * 设置预定执行时间
     * */
    public Event scheduled(Date scheduled) {
        this.scheduled = scheduled;
        return this;
    }

    /**
     * 获取已派发次数（接收时专用）
     * */
    public int times() {
        return times;
    }

    /**
     * 设置已派发次数
     * */
    public Event times(int times) {
        this.times = times;
        return this;
    }

    /**
     * 获取质量：0 只发一次；1 最少发一次；2 发一次并且不重复；（兼容mqtt）
     * */
    public int qos(){return qos;}
    /**
     * 设置质量
     * */
    public Event qos(int qos) {
        this.qos = qos;
        return this;
    }

    /**
     * 获取保留的：是否保留最后一条（兼容mqtt）
     * */
    public boolean retained(){return retained;}
    /**
     * 设置保留的
     * */
    public Event retained(boolean retained){
        this.retained = retained;
        return this;
    }

    /**
     * 获取通道：用于同时支持多个消息队列框架，区分通道
     * */
    public String channel(){
        return channel;
    }
    /**
     * 设置通道
     * */
    public Event channel(String channel){
        this.channel =channel;
        return this;
    }
}
