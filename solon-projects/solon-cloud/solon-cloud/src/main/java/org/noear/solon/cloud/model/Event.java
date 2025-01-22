/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.cloud.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 事件模型
 *
 * @author noear
 * @since 1.2
 */
public class Event implements Serializable {
    private String group;
    private String topic;
    private String content;
    private String key;
    private String tags;
    private Date scheduled;
    private int times;
    private Map<String, String> meta;

    private transient String channel;
    private transient int qos = 1;
    private transient boolean retained = false;
    private transient boolean broadcast = false;
    private transient EventTran tran;

    public Event() {
        //序列化需要
    }

    /**
     * @param topic   主题
     * @param content 内容
     */
    public Event(String topic, String content) {
        this.topic = topic;
        this.content = content;
    }

    /**
     * 元信息
     */
    public Map<String, String> meta() {
        if (meta == null) {
            meta = new HashMap<>();
        }

        return meta;
    }


    /**
     * 元信息推出
     */
    public Event metaPut(String key, String value) {
        meta().put(key, value);
        return this;
    }


    /**
     * 获取事务
     */
    public EventTran tran() {
        return tran;
    }

    /**
     * 配置事务
     */
    public Event tran(EventTran tran) {
        this.tran = tran;
        return this;
    }


    /**
     * 获取主题
     */
    public String topic() {
        return topic;
    }

    /**
     * 获取内容
     */
    public String content() {
        return content;
    }

    /**
     * 获取事件键
     */
    public String key() {
        return key;
    }

    /**
     * 设置事件键
     */
    public Event key(String key) {
        this.key = key;
        return this;
    }

    /**
     * 获取检索标签
     */
    public String tags() {
        return tags;
    }

    /**
     * 设置检索标签
     */
    public Event tags(String tags) {
        this.tags = tags;
        return this;
    }

    /**
     * 获取预定执行时间（派发时专用）
     */
    public Date scheduled() {
        return scheduled;
    }

    /**
     * 设置预定执行时间
     */
    public Event scheduled(Date scheduled) {
        this.scheduled = scheduled;
        return this;
    }

    /**
     * 获取已派发次数（接收时专用）
     */
    public int times() {
        return times;
    }

    /**
     * 设置已派发次数
     */
    public Event times(int times) {
        this.times = times;
        return this;
    }

    /**
     * 获取质量：0 只发一次；1 最少发一次；2 发一次并且不重复；（兼容mqtt,kafka）
     */
    public int qos() {
        return qos;
    }

    /**
     * 设置质量
     */
    public Event qos(int qos) {
        this.qos = qos;
        return this;
    }

    /**
     * 是否保留的：是否保留最后一条（兼容mqtt）
     */
    public boolean retained() {
        return retained;
    }

    /**
     * 设置保留的
     */
    public Event retained(boolean retained) {
        this.retained = retained;
        return this;
    }


    /**
     * 是否广播的
     */
    public boolean broadcast() {
        return broadcast;
    }

    /**
     * 设置广播的
     */
    public Event broadcast(boolean broadcast) {
        this.broadcast = broadcast;
        return this;
    }

    /**
     * 获取分组
     */
    public String group() {
        return group;
    }

    /**
     * 设置分组
     */
    public Event group(String group) {
        this.group = group;
        return this;
    }

    /**
     * 获取通道：用于同时支持多个消息队列框架，区分通道
     */
    public String channel() {
        return channel;
    }

    /**
     * 设置通道
     */
    public Event channel(String channel) {
        this.channel = channel;
        return this;
    }

    @Override
    public String toString() {
        return "Event{" +
                "key='" + key + '\'' +
                ", topic='" + topic + '\'' +
                ", tags='" + tags + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}