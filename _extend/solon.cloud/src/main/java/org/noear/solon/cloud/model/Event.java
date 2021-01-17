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
     * 事件id
     * */
    public String id;

    /**
     * 队列
     * */
    public String queue;

    /**
     * 主题
     * */
    public String topic;
    /**
     * 内容
     * */
    public String content;

    /**
     * 标签
     * */
    public String tags;

    /**
     * 预定执行时间
     * */
    public Date scheduled;
}
