/*******************************************************************************
 * Copyright (c) 2017-2020, org.smartboot. All rights reserved.
 * project name: smart-http
 * file name: Attachment.java
 * Date: 2020-01-01
 * Author: sandao (zhengjunweimail@163.com)
 ******************************************************************************/

package org.smartboot.http.utils;

/**
 * @author 三刀
 * @version V1.0 , 2018/6/1
 */
public class Attachment {

    /**
     * 附件值
     */
    private Object[] values = new Object[8];

    /**
     * 存储附件
     *
     * @param key   附件Key
     * @param value 附件值
     * @param <T>   附件值
     */
    public <T> void put(AttachKey<T> key, T value) {
        int index = key.getIndex();
        if (index > values.length) {
            Object[] old = values;
            int i = 1;
            do {
                i <<= 1;
            } while (i < index);
            values = new Object[i];
            System.arraycopy(old, 0, values, 0, old.length);
        }
        values[index] = value;
    }

    /**
     * 获取附件对象
     *
     * @param key 附件Key
     * @param <T> 附件值
     * @return 附件值
     */
    public <T> T get(AttachKey<T> key) {
        return (T) values[key.getIndex()];
    }

    /**
     * 移除附件
     *
     * @param key 附件Key
     * @param <T> 附件值
     */
    public <T> void remove(AttachKey<T> key) {
        values[key.getIndex()] = null;
    }
}
