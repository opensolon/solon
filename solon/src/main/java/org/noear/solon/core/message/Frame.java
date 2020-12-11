package org.noear.solon.core.message;

/**
 * 帧
 *
 * @author noear
 * @since 1.2
 * */
public class Frame {
    /**
     * 1.消息标志
     * @see FrameFlag
     */
    private final int flag;

    public int flag() {
        return flag;
    }

    public Frame(int flag){
        this.flag = flag;
    }
}
