package org.noear.solon.test.data;

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
    public Class<? extends Annotation> annotationType() {
        return anno.annotationType();
    }

    @Override
    public boolean value() {
        return anno.value();
    }
}
