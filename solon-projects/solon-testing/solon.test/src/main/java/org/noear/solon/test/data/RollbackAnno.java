package org.noear.solon.test.data;

import org.noear.solon.data.tran.TranIsolation;
import org.noear.solon.data.tran.TranPolicy;
import org.noear.solon.test.annotation.Rollback;
import org.noear.solon.test.annotation.TestRollback;

import java.lang.annotation.Annotation;

/**
 * @author noear
 * @see 2.5
 */
public class RollbackAnno implements Rollback {
    TestRollback anno;
    public RollbackAnno(TestRollback anno){
        this.anno = anno;
    }
    @Override
    public TranPolicy policy() {
        return anno.policy();
    }

    @Override
    public TranIsolation isolation() {
        return anno.isolation();
    }

    @Override
    public boolean readOnly() {
        return anno.readOnly();
    }

    @Override
    public String message() {
        return anno.message();
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return anno.annotationType();
    }
}
