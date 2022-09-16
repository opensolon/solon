package org.noear.solon.data.annotation;

import org.noear.solon.data.tran.TranIsolation;
import org.noear.solon.data.tran.TranPolicy;

import java.lang.annotation.Annotation;

/**
 * 事务注解类
 *
 * @author noear
 * @since 1.9
 */
public class TranAnno implements Tran {
    /**
     * 事务传导策略
     */
    private TranPolicy _policy = TranPolicy.required;
    /*
     * 事务隔离等级
     * */
    private TranIsolation _isolation = TranIsolation.unspecified;
    /**
     * 只读事务
     */
    private boolean _readOnly = false;

    @Override
    public TranPolicy policy() {
        return _policy;
    }

    public TranAnno policy(TranPolicy policy){
        _policy = policy;
        return this;
    }

    @Override
    public TranIsolation isolation() {
        return _isolation;
    }
    public TranAnno isolation(TranIsolation isolation){
        _isolation = isolation;
        return this;
    }

    @Override
    public boolean readOnly() {
        return _readOnly;
    }

    public TranAnno readOnly(boolean readOnly){
        _readOnly = readOnly;
        return this;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return Tran.class;
    }
}
