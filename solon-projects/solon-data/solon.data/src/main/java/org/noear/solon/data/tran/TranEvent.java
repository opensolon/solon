package org.noear.solon.data.tran;

import org.noear.solon.data.annotation.Tran;

/**
 * 事务事件
 *
 * @author noear
 * @since 2.4
 */
public class TranEvent {
    /**
     * 阶段
     * */
    private TranPhase phase;
    /**
     * 注解元信息
     * */
    private Tran meta;

    public TranEvent(TranPhase phase, Tran meta){
        this.phase = phase;
        this.meta = meta;
    }

    public TranPhase getPhase() {
        return phase;
    }

    public Tran getMeta() {
        return meta;
    }
}
