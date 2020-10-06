/*******************************************************************************
 * Copyright (c) 2017-2020, org.smartboot. All rights reserved.
 * project name: smart-http
 * file name: ByteArray.java
 * Date: 2020-09-09
 * Author: sandao (zhengjunweimail@163.com)
 ******************************************************************************/

package org.smartboot.http.utils;

/**
 * @author 三刀
 * @version V1.0 , 2020/9/9
 */
public class CharArray {
    private final char[] data;

    private int writeIndex;

    public CharArray(int size) {
        this.data = new char[size];
    }

    public char[] getData() {
        return data;
    }

    public int getWriteIndex() {
        return writeIndex;
    }

    public void setWriteIndex(int writeIndex) {
        this.writeIndex = writeIndex;
    }
}
