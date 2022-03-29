package org.noear.solon.socketd;

/**
 * 监听者代理
 *
 * @author noear
 * @since 1.0
 * */
public class ListenerManager {

    /**
     * 全局对象
     * */
    static final ListenerPipeline pipeline;

    /**
     * 获取全局对象
     * */
    public static ListenerPipeline getPipeline() {
        return pipeline;
    }

    static {
        pipeline = new ListenerPipeline();
        pipeline.next(new ListenerProxy());
    }

}
