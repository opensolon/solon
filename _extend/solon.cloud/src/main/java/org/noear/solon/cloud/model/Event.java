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
     * 事件唯一标识
     * */
    private final String key;

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

    public Event(String key, String queue, String topic, String content){
        this.key = key;
        this.queue = queue;
        this.topic = topic;
        this.content = content;
    }

    public String getKey() {
        return key;
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
