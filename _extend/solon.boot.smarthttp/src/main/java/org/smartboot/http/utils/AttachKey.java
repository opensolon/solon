/*******************************************************************************
 * Copyright (c) 2017-2020, org.smartboot. All rights reserved.
 * project name: smart-http
 * file name: AttachKey.java
 * Date: 2020-01-01
 * Author: sandao (zhengjunweimail@163.com)
 ******************************************************************************/

package org.smartboot.http.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 三刀
 * @version V1.0 , 2018/6/2
 */
public final class AttachKey<T> {
    /**
     * 支持附件数量上限
     */
    public static final int MAX_ATTACHE_COUNT = 128;
    /**
     * 缓存同名Key
     */
    private static final ConcurrentMap<String, AttachKey> NAMES = new ConcurrentHashMap<>();
    /**
     * 索引构造器
     */
    private static final AtomicInteger INDEX_BUILDER = new AtomicInteger(0);
    /**
     * 附件名称
     */
    private final String key;
    /**
     * 附件索引
     */
    private final int index;

    private AttachKey(String key) {
        this.key = key;
        this.index = INDEX_BUILDER.getAndIncrement();
        if (this.index < 0 || this.index >= MAX_ATTACHE_COUNT) {
            throw new RuntimeException("too many attach key");
        }
    }

    public static <T> AttachKey<T> valueOf(String name) {
        AttachKey<T> option = NAMES.get(name);
        if (option == null) {
            option = new AttachKey<T>(name);
            AttachKey<T> old = NAMES.putIfAbsent(name, option);
            if (old != null) {
                option = old;
            }
        }
        return option;
    }

    public String getKey() {
        return key;
    }


    int getIndex() {
        return index;
    }
}
