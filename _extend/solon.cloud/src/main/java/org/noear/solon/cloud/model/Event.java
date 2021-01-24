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
     * 组
     * */
    private final String group;

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

    public Event(String group, String topic, String content){
        this.group = group;
        this.topic = topic;
        this.content = content;
    }

    public String group() {
        return group;
    }

    public String topic() {
        return topic;
    }

    public String content() {
        return content;
    }


    public String key() {
        return key;
    }

    public Event key(String key) {
        this.key = key;
        return this;
    }

    public String tags() {
        return tags;
    }

    public Event tags(String tags) {
        this.tags = tags;
        return this;
    }

    public Date scheduled() {
        return scheduled;
    }

    public Event scheduled(Date scheduled) {
        this.scheduled = scheduled;
        return this;
    }

    public int times() {
        return times;
    }

    public Event times(int times) {
        this.times = times;
        return this;
    }
}
