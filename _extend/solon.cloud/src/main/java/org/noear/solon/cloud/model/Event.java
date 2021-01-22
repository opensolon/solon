package org.noear.solon.cloud.model;

import java.util.Date;

/**
 * 事件模型
 *
 * @author noear
 * @since 1.2
 */
public class Event {
    /**
     * 队列
     * */
    private final String queue;

    /**
     * 主题
     * */
    private final String topic;
    /**
     * 内容
     * */
    private final String content;

    /**
     * 事件唯一标识
     * */
    private String key;

    /**
     * 检索标签
     * */
    private String tags;

    /**
     * 预定执行时间（派发时专用）
     * */
    private Date scheduled;

    /**
     * 已派发次数（接收时专用）
     * */
    private int times;

    public Event(String topic, String content) {
        this("", topic, content);
    }

    public Event(String queue, String topic, String content){
        this.queue = queue;
        this.topic = topic;
        this.content = content;
    }

    public String getQueue() {
        return queue;
    }

    public String getTopic() {
        return topic;
    }

    public String getContent() {
        return content;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Date getScheduled() {
        return scheduled;
    }

    public void setScheduled(Date scheduled) {
        this.scheduled = scheduled;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }
}
