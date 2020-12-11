package org.noear.solon.core.message;

import org.noear.solon.annotation.Note;

/**
 * 帧
 *
 * @author noear
 * @since 1.2
 * */
public class Frame {
    /**
     * 1.消息标志（-2心跳包, -1握手包；0发起包； 1响应包）
     */
    private final int flag;

    @Note("1.消息标志（-2心跳包, -1握手包；0发起包； 1响应包）")
    public int flag() {
        return flag;
    }

    public Frame(int flag){
        this.flag = flag;
    }
}
