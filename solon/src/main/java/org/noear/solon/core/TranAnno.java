package org.noear.solon.core;

import org.noear.solon.annotation.XTran;

public class TranAnno {

    private final String _value;

    private final TranPolicy _policy;

    private final boolean _group;

    public TranAnno(XTran anno) {
        _value = anno.value();
        _policy = anno.policy();
        _group = anno.group();
    }

    public TranAnno(String value) {
        this(value, TranPolicy.required);
    }

    public TranAnno(String value, TranPolicy policy) {
        this(value,policy, false);
    }

    public TranAnno(String value, TranPolicy policy, boolean group) {
        if (value == null) {
            _value = "";
        } else {
            _value = value;
        }
        _policy = policy;
        _group = group;
    }



    /**
     * 数据源标识
     */
    public String value() {
        return _value;
    }

    /**
     * 事务策略
     */
    public TranPolicy policy() {
        return _policy;
    }

    /**
     * 是否为事务组
     */
    public boolean group() {
        return _group;
    }

}
