/*******************************************************************************
 * Copyright (c) 2017-2020, org.smartboot. All rights reserved.
 * project name: smart-http
 * file name: LoggerStatus.java
 * Date: 2020-01-01
 * Author: sandao (zhengjunweimail@163.com)
 ******************************************************************************/

package org.smartboot.http.logging;

/**
 * @author 三刀
 * @version V1.0 , 2020/1/1
 */
enum LoggerStatus {
    /**
     * 未初始化
     */
    UNINITIALIZE,
    /**
     * 临时可用
     */
    TEMP_ENABLED,
    /**
     * 可用
     */
    ENABLED,
    /**
     * 停止中
     */
    STOPING,
    /**
     * 已停止
     */
    STOPED,
    /**
     * 关闭中
     */
    Closing,
    /**
     * 已关闭
     */
    Closed;
}