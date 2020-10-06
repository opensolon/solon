/*******************************************************************************
 * Copyright (c) 2017-2020, org.smartboot. All rights reserved.
 * project name: smart-http
 * file name: SmartDecoder.java
 * Date: 2020-01-01
 * Author: sandao (zhengjunweimail@163.com)
 ******************************************************************************/

package org.smartboot.http.utils;

import java.nio.ByteBuffer;

/**
 * @author 三刀
 * @version V1.0 , 2018/1/28
 */
public interface SmartDecoder {
    /**
     * 解码算法
     *
     * @param byteBuffer
     * @return
     */
    boolean decode(ByteBuffer byteBuffer);

    /**
     * 获取本次解析到的完整数据
     *
     * @return
     */
    ByteBuffer getBuffer();
}
