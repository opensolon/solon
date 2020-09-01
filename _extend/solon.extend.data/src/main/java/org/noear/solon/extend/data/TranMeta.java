package org.noear.solon.extend.data;

import org.noear.solon.annotation.XTran;
import org.noear.solon.core.TranPolicy;

class TranMeta {
    private String _name = "";
    private TranPolicy _policy = TranPolicy.required;
    private boolean _group = false;

    public String name() {
        return _name;
    }

    public TranPolicy policy() {
        return _policy;
    }

    public boolean group() {
        return _group;
    }

    public static TranMeta of(XTran anno) {
        TranMeta tmp = new TranMeta();
        tmp._name = anno.value();
        tmp._policy = anno.policy();
        tmp._group = anno.group();

        return tmp;
    }

    public static TranMeta of(TranPolicy policy, String name, boolean group) {
        TranMeta tmp = new TranMeta();
        if (name != null) {
            tmp._name = name;
        }
        tmp._policy = policy;
        tmp._group = group;

        return tmp;
    }

    static TranMeta TRAN_GROUP = of(TranPolicy.required, "", true);

    static TranMeta TRAN_MANDATORY = of(TranPolicy.mandatory, "", false);
    static TranMeta TRAN_NOT = of(TranPolicy.not_supported, "", false);
    static TranMeta TRAN_NEVER = of(TranPolicy.never, "", false);
}
